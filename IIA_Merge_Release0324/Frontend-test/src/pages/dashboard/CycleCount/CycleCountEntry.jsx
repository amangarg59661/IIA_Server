import { Card, message, Select, Input, Button, Table, Tag } from "antd";
import React, { useState , useEffect} from "react";
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
    // custodianId: "",
  });

  const [formData, setFormData] = useState({
    countType: "MANUAL",
    locatorId: "",
    // sweepCustodianId: "",
    manualItems: [], // built up locally before initiate
    cycleCountId: "", // set once initiated
    lines: [], // set once initiated (from SearchByCycleCountId)
    remarksHeader: "",
  });

  const initiated = !!formData.cycleCountId;
const isReadOnly = !!formData.status && formData.status !== "DRAFT";
  // ---- LOV wiring (locator + custodian) -------------------------------------
  // TODO: createLOV — see file header note #1.
  const { lovValues: locatorLOV = [], loading: loadingLocators } = useLOVValues(1, 'locator');
  // const { lovValues: custodianLOV = [], loading: loadingCustodians } = useLOVValues(1, 'locator');

  const locatorDropdown = locatorLOV
    .filter((item) => item.isActive === true)
    .map((item) => ({ label: item.lovDisplayValue, value: item.lovValue }));

  // const custodianDropdown = custodianLOV
  //   .filter((item) => item.isActive === true)
  //   .map((item) => ({ label: item.lovDisplayValue, value: item.lovValue }));

  // ---- MANUAL item picker (before initiation) --------------------------------

  const addManualItem = () => {
    if (!manualItemDraft.materialCode || !formData.locatorId ) {
      message.warning("Material code, locator are all required to add an item.");
      return;
    }
    setFormData((prev) => ({
      ...prev,
      manualItems: [
        ...prev.manualItems,
        { ...manualItemDraft, locatorId: prev.locatorId },
      ],
    }));
    setManualItemDraft({ materialCode: "", materialDesc: "", locatorId: "" });
  };

  const removeManualItem = (index) => {
    setFormData((prev) => ({
      ...prev,
      manualItems: prev.manualItems.filter((_, i) => i !== index),
    }));
  };

  // ---- counted-quantity grid (after initiation) ------------------------------
  // Same tuple convention as ServiceInspection.jsx's handleChange.
  // const handleLineChange = (fieldName, value) => {
  //   if (typeof fieldName === "string") {
  //     setFormData((prev) => ({ ...prev, [fieldName]: value }));
  //     return;
  //   }
  //   setFormData((prev) => {
  //     const prevLines = [...prev.lines];
  //     prevLines[fieldName[1]][fieldName[2]] = value;
  //     return { ...prev, lines: prevLines };
  //   });
  // };

  const handleLineChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
      return;
    }
    setFormData((prev) => {
      const prevLines = [...prev.lines];
      const row = { ...prevLines[fieldName[1]] };
      row[fieldName[2]] = value;

      if (fieldName[2] === "countedQty") {
        const countedQty = parseFloat(value);
        const systemQty = parseFloat(row.systemQtySnapshot) || 0;
        const unitPrice = parseFloat(row.unitPriceSnapshot) || 0;

        if (!isNaN(countedQty)) {
          const varianceQty = countedQty - systemQty;
          row.varianceQty = varianceQty;
          row.varianceValue = varianceQty * unitPrice;
        } else {
          row.varianceQty = "";
          row.varianceValue = "";
        }
      }

      prevLines[fieldName[1]] = row;
      return { ...prev, lines: prevLines };
    });
  };

  // ---- material master (dedicated fetch — see file header note #3) ---------
const [materialOptions, setMaterialOptions] = useState([]);
const [materialMap, setMaterialMap] = useState({});
const [loadingMaterials, setLoadingMaterials] = useState(false);

const [cycleCountSearchValue, setCycleCountSearchValue] = useState("");
const [cycleCountOptions, setCycleCountOptions] = useState([]);
const [loadingCycleCountSearch, setLoadingCycleCountSearch] = useState(false);
const [loadingCycleCountLoad, setLoadingCycleCountLoad] = useState(false);


const handleSearchCycleCounts = async (value) => {
  if (!value) {
    message.warning("Enter a cycle count ID, locator, or status to search.");
    return;
  }
  try {
    setLoadingCycleCountSearch(true);
    const { data } = await axios.get("/api/process-controller/searchCycleCount", {
      params: { value },
    });
    const results = data?.responseData || [];
    setCycleCountOptions(
      results.map((item) => ({
        label: item.status === "DRAFT" ? `${item.cycleCountId} [DRAFT]` : `${item.cycleCountId} [${item.status}]`,
        value: item.cycleCountId,
      }))
    );
    if (results.length === 0) message.warning("No matching cycle counts found.");
  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error searching cycle counts.");
  } finally {
    setLoadingCycleCountSearch(false);
  }
};

const handleLoadCycleCount = async (cycleCountId) => {
  try {
    setLoadingCycleCountLoad(true);
    const { data } = await axios.get(`/api/process-controller/SearchByCycleCountId?cycleCountId=${cycleCountId}`);
    const header = data?.responseData || {};
    const lines = (header.lines || []).map((l) => ({
      ...l,
      countedQty: l.countedQty ?? "",
      remarks: l.remarks ?? "",
    }));
    // setFormData((prev) => ({
    //   ...prev,
    //   cycleCountId,
    //   countType: header.countType || prev.countType,
    //   locatorId: header.locatorId || prev.locatorId,
    //   lines,
    // }));
    setFormData((prev) => ({
      ...prev,
      cycleCountId,
      countType: header.countType || prev.countType,
      locatorId: header.locatorId || prev.locatorId,
      status: header.status,
      lines,
    }));
    message.success(`Loaded cycle count ${cycleCountId}.`);
  } catch (error) {
    message.error(error?.response?.data?.responseStatus?.message || "Error loading cycle count.");
  } finally {
    setLoadingCycleCountLoad(false);
  }
};

useEffect(() => {
  const fetchMaterials = async () => {
    try {
      setLoadingMaterials(true);
      const { data } = await axios.get("/api/material-master");
      // const materials = data?.responseData || [];
      const materials = (data?.responseData || []).filter((m) => !m.asset_Flag);
      const map = {};
      materials.forEach((m) => {
        map[m.materialCode] = m;
      });
      setMaterialMap(map);
      setMaterialOptions(
        materials.map((m) => ({
          label: `${m.materialCode} - ${m.description}`,
          value: m.materialCode,
        }))
      );
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to load material list.");
    } finally {
      setLoadingMaterials(false);
    }
  };
  fetchMaterials();
}, []);

  // ---- initiate ---------------------------------------------------------------

  const handleInitiate = async () => {
    if (!formData.locatorId) {
      message.warning("Please select a locator.");
      return;
    }
    // if (formData.countType === "SWEEP" && !formData.sweepCustodianId) {
    //   message.warning("Sweep custodian is required for a SWEEP count.");
    //   return;
    // }
    if (formData.countType === "MANUAL" && formData.manualItems.length === 0) {
      message.warning("Add at least one item for a MANUAL count.");
      return;
    }

    try {
      setSubmitBtnLoading(true);

      const initiatePayload = {
        countType: formData.countType,
        locatorId: formData.locatorId,
        // sweepCustodianId: formData.countType === "SWEEP" ? formData.sweepCustodianId : null,
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
        // ...(formData.countType === "SWEEP"
        //   ? [
        //       {
        //         name: "sweepCustodianId",
        //         label: "Sweep Custodian",
        //         type: "select",
        //         span: 1,
        //         required: true,
        //         disabled: initiated,
        //         loading: loadingCustodians,
        //         options: custodianDropdown,
        //       },
        //     ]
        //   : []),
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
        { name: "varianceQty", label: "Variance Qty", type: "text", disabled: true },
        { name: "varianceValue", label: "Variance Value", type: "text", disabled: true },
        { name: "remarks", label: "Remarks", type: "text", span: 2 },
      ],
    },
  ];

  return (
    <Card className="a4-container">
      <Heading title="Cycle Count" />
      {initiated && formData.status && (
        <Tag
          color={{ DRAFT: "blue", "AWAITING APPROVAL": "orange", APPROVED: "green", REJECTED: "red" }[formData.status] || "default"}
          style={{ marginBottom: 12 }}
        >
          {formData.status}
        </Tag>
      )}
      <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
  <Input
    placeholder="Search by Cycle Count ID / Locator / Status"
    value={cycleCountSearchValue}
    onChange={(e) => setCycleCountSearchValue(e.target.value)}
    onPressEnter={() => handleSearchCycleCounts(cycleCountSearchValue)}
    style={{ maxWidth: 320 }}
  />
  <Button loading={loadingCycleCountSearch} onClick={() => handleSearchCycleCounts(cycleCountSearchValue)}>
    Search
  </Button>
  <Select
    showSearch
    placeholder="Select a result to load"
    style={{ minWidth: 260 }}
    options={cycleCountOptions}
    loading={loadingCycleCountLoad}
    onChange={(value) => handleLoadCycleCount(value)}
  />
</div>
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(setupSection, handleLineChange, formData, "", null, setFormData)}

        {formData.countType === "MANUAL" && !initiated && (
          <div style={{ margin: "16px 0" }}>
            <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
              {/* <Input
                placeholder="Material Code"
                value={manualItemDraft.materialCode}
                onChange={(e) => setManualItemDraft((p) => ({ ...p, materialCode: e.target.value }))}
              />
              <Input
                placeholder="Material Description"
                value={manualItemDraft.materialDesc}
                onChange={(e) => setManualItemDraft((p) => ({ ...p, materialDesc: e.target.value }))}
              /> */}
              <Select
  showSearch
  placeholder="Material Code"
  style={{ minWidth: 220 }}
  loading={loadingMaterials}
  options={materialOptions}
  optionFilterProp="label"
  value={manualItemDraft.materialCode || undefined}
  onChange={(value) => {
    const material = materialMap[value];
    setManualItemDraft((p) => ({
      ...p,
      materialCode: value,
      materialDesc: material?.description || "",
    }));
  }}
/>
<Input
  placeholder="Material Description"
  value={manualItemDraft.materialDesc}
  disabled
/>
              {/* <Select
                placeholder="Custodian"
                style={{ minWidth: 180 }}
                loading={loadingCustodians}
                options={custodianDropdown}
                value={manualItemDraft.custodianId || undefined}
                onChange={(value) => setManualItemDraft((p) => ({ ...p, custodianId: value }))}
              /> */}
              <Button onClick={addManualItem}>Add Item</Button>
            </div>
            <Table
              size="small"
              pagination={false}
              dataSource={formData.manualItems.map((it, i) => ({ ...it, key: i }))}
              columns={[
                { title: "Material Code", dataIndex: "materialCode" },
                { title: "Description", dataIndex: "materialDesc" },
                // { title: "Custodian", dataIndex: "custodianId" },
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
          submitBtnEnabled={!isReadOnly}
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
