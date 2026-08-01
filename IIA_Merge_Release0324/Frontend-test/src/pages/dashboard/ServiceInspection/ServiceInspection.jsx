import { Card, message } from "antd";
import React, { useCallback, useEffect, useRef, useState } from "react";
import Heading from "../../../components/DKG_Heading";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import ButtonContainer from "../../../components/ButtonContainer";
import { useReactToPrint } from "react-to-print";
import axios from "axios";
import { useSelector } from "react-redux";
import CustomModal from "../../../components/CustomModal";
import { useLocation } from "react-router-dom";

/**
 * ===================== PLACEHOLDER API CONTRACT =====================
 * All four endpoints below are NOT built yet — this file is for review
 * so the backend can be built to match exactly what this form sends/expects.
 *
 * 1) GET /api/service-inspection/soDetails?soId={soId}
 *    -> { responseData: {
 *          soId, vendorName, projectName, soAmount,
 *          materials: [{ materialCode, materialDescription, quantity, rate }]
 *        }}
 *    Triggered when the user searches an SO id. quantity/rate come straight
 *    off the SO's own material lines — used to prefill orderedQty/rate per row.
 *
 * 2) GET /api/service-inspection/details?inspectionProcessId={id}
 *    -> { responseData: {
 *          inspectionProcessId, soId, vendorName, projectName, soAmount,
 *          inspectionDate, inspectedBy, remarks, currentStatus,
 *          materials: [{ materialCode, materialDescription, orderedQty, rate,
 *                         acceptedQty, rejectedQty, rejectionType, rejectReason,
 *                         supportingDocBase64 }]
 *        }}
 *    Loads an existing (in-progress or past) inspection for edit/view.
 *
 * 3) POST /api/service-inspection/save   (new inspection, no inspectionProcessId yet)
 * 4) POST /api/service-inspection/update (editing an existing inspectionProcessId)
 *    Both take the full formData shape below as payload (+ createdBy, role) and
 *    return -> { responseData: { inspectionProcessId } }
 * =======================================================================
 */

const ServiceInspection = () => {
  const printRef = useRef();
  const handlePrint = useReactToPrint({
    content: () => printRef.current,
  });

  const location = useLocation();
  // Entry point A: navigated here from Queue Request with an approved SO to inspect.
  const soIdFromQueue = location?.state?.soId || null;
  // Entry point B: navigated here to edit/view an existing (in-progress) inspection.
  const inspectionProcessIdFromQueue = location?.state?.inspectionProcessId || null;

  const [modalOpen, setModalOpen] = useState(false);
  const [submitBtnLoading, setSubmitBtnLoading] = useState(false);
  const [formData, setFormData] = useState({
    soId: soIdFromQueue || "",
    inspectionProcessId: inspectionProcessIdFromQueue || "",
    materials: [],
  });

  const { userId, role } = useSelector((state) => state.auth);

  const handleChange = (fieldName, value) => {
    if (typeof fieldName === "string") {
      setFormData((prev) => ({ ...prev, [fieldName]: value }));
    } else {
      // [_, rowIndex, childFieldName] — same tuple convention GoodsInspection.jsx uses
      setFormData((prev) => {
        const prevMaterials = [...prev.materials];

        if (fieldName[2] === "acceptedQty") {
          const acceptedQty = parseFloat(value);
          const orderedQty = parseFloat(prevMaterials[fieldName[1]].orderedQty);
          const rejectedQty = orderedQty - acceptedQty;

          if (rejectedQty + acceptedQty !== orderedQty) {
            message.error("Accepted + rejected quantity must equal the ordered quantity.");
            prevMaterials[fieldName[1]].acceptedQty = 0;
            prevMaterials[fieldName[1]].rejectedQty = 0;
            prevMaterials[fieldName[1]].rejectionType = "";
            prevMaterials[fieldName[1]].rejectReason = "";
            return { ...prev, materials: prevMaterials };
          }

          prevMaterials[fieldName[1]].acceptedQty = acceptedQty;
          prevMaterials[fieldName[1]].rejectedQty = rejectedQty;

          if (rejectedQty <= 0) {
            prevMaterials[fieldName[1]].rejectionType = "";
            prevMaterials[fieldName[1]].rejectReason = "";
          }

          return { ...prev, materials: prevMaterials };
        }

        prevMaterials[fieldName[1]][fieldName[2]] = value;
        return { ...prev, materials: prevMaterials };
      });
    }
  };

  const handleSoSearch = useCallback(async (soId) => {
    if (!soId) {
      message.warning("Please enter a valid SO ID.");
      return;
    }
    try {
      // PLACEHOLDER — see contract #1 above
      const { data } = await axios.get(`/api/service-inspection/soDetails?soId=${soId}`);
      const res = data?.responseData || {};

      setFormData((prev) => ({
        ...prev,
        soId: res.soId || soId,
        vendorName: res.vendorName || "",
        projectName: res.projectName || "",
        soAmount: res.soAmount || "",
        materials: (res.materials || []).map((line) => ({
          materialCode: line.materialCode,
          materialDescription: line.materialDescription,
          orderedQty: line.quantity,
          rate: line.rate,
          acceptedQty: "",
          rejectedQty: "",
          rejectionType: "",
          rejectReason: "",
          supportingDocBase64: "",
        })),
      }));
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching Service Order data.");
    }
  }, []);

  const handleSiSearch = useCallback(async (inspectionProcessId) => {
    if (!inspectionProcessId) {
      message.warning("Please enter a valid Inspection No.");
      return;
    }
    try {
      // PLACEHOLDER — see contract #2 above
      const { data } = await axios.get(
        `/api/service-inspection/details?inspectionProcessId=${inspectionProcessId}`
      );
      setFormData((prev) => ({
        ...prev,
        ...data?.responseData,
        inspectionProcessId: data?.responseData?.inspectionProcessId || inspectionProcessId,
      }));
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Error fetching Service Inspection data.");
    }
  }, []);

  const onFinish = async () => {
    const payload = { ...formData, createdBy: userId, role };
    try {
      setSubmitBtnLoading(true);
      const isUpdate = !!formData.inspectionProcessId;
      // PLACEHOLDER — see contracts #3/#4 above
      const apiUrl = isUpdate ? "/api/service-inspection/update" : "/api/service-inspection/save";

      const { data } = await axios.post(apiUrl, payload);

      setFormData((prev) => ({
        ...prev,
        inspectionProcessId: data?.responseData?.inspectionProcessId || prev.inspectionProcessId,
      }));

      localStorage.removeItem("serviceInspectionDraft");

      if (isUpdate) {
        message.success("Updated Successfully.");
      } else {
        setModalOpen(true);
      }
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to save Service Inspection.");
    } finally {
      setSubmitBtnLoading(false);
    }
  };

  const generalDtls = [
    {
      heading: "Order Details",
      colCnt: 5,
      fieldList: [
        {
          name: "soId",
          label: "SO ID",
          type: "search",
          span: 2,
          required: true,
          onSearch: () => handleSoSearch(formData.soId),
        },
        {
          name: "inspectionProcessId",
          label: "Inspection No.",
          type: "search",
          span: 2,
          onSearch: () => handleSiSearch(formData.inspectionProcessId),
        },
        {
          name: "inspectionDate",
          label: "Inspection Date",
          type: "date",
          required: true,
        },
      ],
    },
    {
      heading: "Vendor & SO Details",
      colCnt: 4,
      fieldList: [
        {
          name: "vendorName",
          label: "Vendor Name",
          type: "text",
          disabled: true,
          span: 2,
          required: true,
        },
        {
          name: "projectName",
          label: "Project Name",
          type: "text",
          disabled: true,
          span: 2,
        },
        {
          name: "soAmount",
          label: "SO Amount (INR)",
          type: "text",
          disabled: true,
          span: 2,
        },
        {
          name: "inspectedBy",
          label: "Inspected By",
          type: "text",
          span: 2,
          required: true,
        },
      ],
    },
    {
      heading: "Service Line Details",
      name: "materials",
      colCnt: 4,
      children: [
        {
          name: "materialCode",
          label: "Material Code",
          type: "text",
          disabled: true,
          span: 2,
          required: true,
        },
        {
          name: "materialDescription",
          label: "Description",
          type: "text",
          disabled: true,
          span: 3,
          required: true,
        },
        {
          name: "orderedQty",
          label: "Ordered Qty",
          type: "text",
          disabled: true,
          required: true,
        },
        {
          name: "rate",
          label: "Rate (INR)",
          type: "text",
          disabled: true,
        },
        {
          name: "acceptedQty",
          label: "Accepted Qty",
          type: "text",
          required: true,
        },
        {
          name: "rejectedQty",
          label: "Rejected Qty",
          type: "text",
          disabled: true,
          required: true,
        },
        // Same convention as GoodsInspection.jsx: appears for every row once ANY
        // row has a rejected qty > 0, not per-row conditional.
        ...(formData.materials?.some((item) => parseFloat(item.rejectedQty) > 0)
          ? [
              {
                name: "rejectionType",
                label: "Rejection Type",
                type: "select",
                span: 2,
                required: formData.materials?.some((item) => parseFloat(item.rejectedQty) > 0),
                options: [
                  { label: "Redo Required", value: "redo_required" },
                  { label: "Not Payable", value: "not_payable" },
                ],
              },
              {
                name: "rejectReason",
                label: "Reason for Rejection",
                type: "text",
                span: 2,
                required: formData.materials?.some((item) => parseFloat(item.rejectedQty) > 0),
              },
            ]
          : []),
        {
          name: "supportingDocBase64",
          label: "Supporting Document",
          type: "image",
          span: 2,
        },
      ],
    },
    {
      heading: "Remarks",
      colCnt: 3,
      fieldList: [
        {
          name: "remarks",
          label: "Remarks",
          type: "text",
          span: 3,
        },
      ],
    },
  ];

  useEffect(() => {
    const draft = localStorage.getItem("serviceInspectionDraft");
    if (draft) {
      setFormData(JSON.parse(draft));
      message.success("Form loaded from draft.");
    }
  }, []);

  useEffect(() => {
    if (inspectionProcessIdFromQueue) {
      handleSiSearch(inspectionProcessIdFromQueue);
    } else if (soIdFromQueue) {
      handleSoSearch(soIdFromQueue);
    }
  }, [inspectionProcessIdFromQueue, soIdFromQueue, handleSiSearch, handleSoSearch]);

  return (
    <Card className="a4-container" ref={printRef}>
      <Heading title="Service Inspection" />
      <CustomForm formData={formData} onFinish={onFinish}>
        {renderFormFields(generalDtls, handleChange, formData, "", null, setFormData, handleSoSearch)}
        <ButtonContainer
          onFinish={onFinish}
          formData={formData}
          draftDataName="serviceInspectionDraft"
          submitBtnLoading={submitBtnLoading}
          submitBtnEnabled
          printBtnEnabled
          draftBtnEnabled
          handlePrint={handlePrint}
        />
      </CustomForm>
      <CustomModal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        title="Service Inspection"
        processNo={formData?.inspectionProcessId}
      />
    </Card>
  );
};

export default ServiceInspection;
