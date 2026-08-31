import { Card, message, Select, Input, Button, Table } from "antd";
import React, { useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { useLOVValues } from "../../../hooks/useLOVValues";

/**
 * Single continuous page: pick MANUAL/SWEEP + locator, initiate the count
 * (which snapshots system quantities server-side), then immediately enter
 * counted quantities against the returned lines and submit.
 *
 * Does NOT touch approval — that happens via the generic workflow queue
 * (workflowId 12, Inventory tab / Queue3 / CycleCountQueue.jsx) once
 * submitCycleCount kicks off initiateWorkflow server-side.
 *
 * TWO THINGS STILL FLAGGED, NOT YET CONFIRMED:
 * 1. createLOV()'s exact signature/return shape — assumed to match the
 *    { lovValues, loading } shape useLOVValues() returns elsewhere in the
 *    app (see Indent1.jsx), since locator/custodian come from separate APIs
 *    (not the generic LOV designator system). Swap the two calls below once
 *    the real signature is settled — the .filter/.map after them should
 *    keep working unchanged as long as items still carry isActive/
 *    lovDisplayValue/lovValue-shaped fields.
 * 2. renderFormFields()'s positional args are copied verbatim from
 *    ServiceInspection.jsx (sections, handleChange, formData, "", null,
 *    setFormData) — the empty-string 4th arg and any significance beyond
 *    SI's specific use aren't confirmed.
 */
const CycleCountEntry = () => {
  const { userId } = useSelector((state) => state.auth);

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [manualItemDraft, setManualItemDraft] = useState({
    materialCode: "",
    materialDesc: "",
    locatorId: "",
    custodianId: "",
  });

  const [formData, setFormData] = useState({
    countType: "MANUAL",
    locatorId: "",
    sweepCustodianId: "",
    manualItems: [], // built up locally before initiate
    cycleCountId: "", // set once initiated
    lines: [], // set once initiated (from SearchByCycleCountId)
    remarksHeader: "",
  });

  const initiated = !!formData.cycleCountId;

  // ---- LOV wiring (locator + custodian) -------------------------------------
  // TODO: createLOV — see file header note #1.
  const { lovValues: locatorLOV = [], loading: loadingLocators } = useLOVValues(1, 'locator');
  const { lovValues: custodianLOV = [], loading: loadingCustodians } = useLOVValues(1, 'locator');

  const locatorDropdown = locatorLOV
    .filter((item) => item.isActive === true)
    .map((item) => ({ label: item.lovDisplayValue, value: item.lovValue }));

  const custodianDropdown = custodianLOV
    .filter((item) => item.isActive === true)
    .map((item) => ({ label: item.lovDisplayValue, value: item.lovValue }));

  // ---- MANUAL item picker (before initiation) --------------------------------

  const addManualItem = () => {
    if (!manualItemDraft.materialCode || !formData.locatorId || !manualItemDraft.custodianId) {
      message.warning("Material code, locator, and custodian are all required to add an item.");
      return;
    }
    setFormData((prev) => ({
      ...prev,
      manualItems: [
        ...prev.manualItems,
        { ...manualItemDraft, locatorId: prev.locatorId },
      ],
    }));
    setManualItemDraft({ materialCode: "", materialDesc: "", locatorId: "", custodianId: "" });
  };

  const removeManualItem = (index) => {
    setFormData((prev) => ({
      ...prev,
      manualItems: prev.manualItems.filter((_, i) => i !== index),
    }));
  };

  // ---- counted-quantity grid (after initiation) ------------------------------
  // Same tuple convention as ServiceInspection.jsx's handleChange.
  const handleLineChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
      return;
    }
    setFormData((prev) => {
      const prevLines = [...prev.lines];
      prevLines[fieldName[1]][fieldName[2]] = value;
      return { ...prev, lines: prevLines };
    });
  };

  // ---- initiate ---------------------------------------------------------------

  const handleInitiate = async () => {
    if (!formData.locatorId) {
      message.warning("Please select a locator.");
      return;
    }
    if (formData.countType === "SWEEP" && !formData.sweepCustodianId) {
      message.warning("Sweep custodian is required for a SWEEP count.");
      return;
    }
    if (formData.countType === "MANUAL" && formData.manualItems.length === 0) {
      message.warning("Add at least one item for a MANUAL count.");
      return;
    }

    try {
      setSubmitBtnLoading(true);

      const initiatePayload = {
        countType: formData.countType,
        locatorId: formData.locatorId,
        sweepCustodianId: formData.countType === "SWEEP" ? formData.sweepCustodianId : null,
        manualItems: formData.countType === "MANUAL" ? formData.manualItems : null,
      };

      const { data } = await axios.post("/api/process-controller/initiateCycleCount", initiatePayload);
      const cycleCountId = data?.responseData?.processNo;

      // initiateCycleCount only returns the ID — pull the generated lines
      // (with their dtlIds) before the counting grid can render.
      const detail = await axios.get(`/api/process-controller/SearchByCycleCountId?cycleCountId=${cycleCountId}`);
      const lines = (detail?.data?.responseData?.lines || []).map((l) => ({
        ...l,
        countedQty: "",
        remarks: "",
      }));

      setFormData((prev) => ({ ...prev, cycleCountId, lines }));
      message.success(`Count ${cycleCountId} started — enter quantities below.`);
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to start cycle count.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  // ---- submit -------------------------------------------------------------------

  const handleSubmitCount = async () => {
    const missing = formData.lines.some((l) => l.countedQty === "" || l.countedQty === null);
    if (missing) {
      message.warning("Every line needs a counted quantity before submitting.");
      return;
    }

    const payload = {
      cycleCountId: formData.cycleCountId,
      countedBy: userId,
      lines: formData.lines.map((l) => ({
        dtlId: l.dtlId,
        countedQty: l.countedQty,
        remarks: l.remarks,
      })),
    };

    try {
      setSubmitBtnLoading(true);
      await axios.post("/api/process-controller/submitCycleCount", payload);
      setModalOpen(true);
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to submit cycle count.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  const onFinish = () => (initiated ? handleSubmitCount() : handleInitiate());

  // ---- declarative sections (setup + counted-qty grid) ---------------------------

  const setupSection = [
    {
      heading: "Count Setup",
      colCnt: 4,
      fieldList: [
        {
          name: "countType",
          label: "Count Type",
          type: "select",
          span: 1,
          required: true,
          disabled: initiated,
          options: [
            { label: "Manual", value: "MANUAL" },
            { label: "Sweep", value: "SWEEP" },
          ],
        },
        {
          name: "locatorId",
          label: "Locator",
          type: "select",
          span: 1,
          required: true,
          disabled: initiated,
          loading: loadingLocators,
          options: locatorDropdown,
        },
        ...(formData.countType === "SWEEP"
          ? [
              {
                name: "sweepCustodianId",
                label: "Sweep Custodian",
                type: "select",
                span: 1,
                required: true,
                disabled: initiated,
                loading: loadingCustodians,
                options: custodianDropdown,
              },
            ]
          : []),
      ],
    },
  ];

  const countingSection = [
    {
      heading: "Counted Quantities",
      name: "lines",
      colCnt: 4,
      children: [
        { name: "materialCode", label: "Material Code", type: "text", disabled: true },
        { name: "materialDesc", label: "Description", type: "text", disabled: true, span: 2 },
        { name: "uom", label: "UOM", type: "text", disabled: true },
        { name: "systemQtySnapshot", label: "System Qty", type: "text", disabled: true },
        { name: "unitPriceSnapshot", label: "Unit Price", type: "text", disabled: true },
        { name: "countedQty", label: "Counted Qty", type: "text", required: true },
        { name: "remarks", label: "Remarks", type: "text", span: 2 },
      ],
    },
  ];

  return (
    <Card className="a4-container">
      <Heading title="Cycle Count" />

      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(setupSection, handleLineChange, formData, "", null, setFormData)}

        {formData.countType === "MANUAL" && !initiated && (
          <div style={{ margin: "16px 0" }}>
            <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
              <Input
                placeholder="Material Code"
                value={manualItemDraft.materialCode}
                onChange={(e) => setManualItemDraft((p) => ({ ...p, materialCode: e.target.value }))}
              />
              <Input
                placeholder="Material Description"
                value={manualItemDraft.materialDesc}
                onChange={(e) => setManualItemDraft((p) => ({ ...p, materialDesc: e.target.value }))}
              />
              <Select
                placeholder="Custodian"
                style={{ minWidth: 180 }}
                loading={loadingCustodians}
                options={custodianDropdown}
                value={manualItemDraft.custodianId || undefined}
                onChange={(value) => setManualItemDraft((p) => ({ ...p, custodianId: value }))}
              />
              <Button onClick={addManualItem}>Add Item</Button>
            </div>
            <Table
              size="small"
              pagination={false}
              dataSource={formData.manualItems.map((it, i) => ({ ...it, key: i }))}
              columns={[
                { title: "Material Code", dataIndex: "materialCode" },
                { title: "Description", dataIndex: "materialDesc" },
                { title: "Custodian", dataIndex: "custodianId" },
                {
                  title: "",
                  render: (_, __, i) => (
                    <Button danger size="small" onClick={() => removeManualItem(i)}>
                      Remove
                    </Button>
                  ),
                },
              ]}
            />
          </div>
        )}

        {initiated &&
          renderFormFields(countingSection, handleLineChange, formData, "", null, setFormData)}

        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
        />
      </CustomForm>

      <CustomModal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        title="Cycle Count"
        processNo={formData?.cycleCountId}
      />
    </Card>
  );
};

export default CycleCountEntry;
