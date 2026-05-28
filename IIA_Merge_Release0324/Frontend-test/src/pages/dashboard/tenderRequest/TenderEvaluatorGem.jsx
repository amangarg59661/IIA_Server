import React, { useEffect, useState, useRef } from "react";
import { Table, Input, Button, Spin, message } from "antd";
import axios from "axios";
import { useSelector } from "react-redux";
import Heading from "../../../components/DKG_Heading";
import FormContainer from "../../../components/DKG_FormContainer";
import FormBody from "../../../components/DKG_FormBody";
import CustomForm from "../../../components/DKG_CustomForm";
import { renderFormFields } from "../../../utils/CommonFunctions";
import Btn from "../../../components/DKG_Btn";
import { baseURL } from "../../../App";
import FileUploadVendor from "../../../components/DKG_Vendor_FileUpload";
import { HistoryOutlined } from '@ant-design/icons';
import AllVendorsQuotationsstatus from "./AllVendorQuotationstatus";

const TenderEvaluatorGem = () => {
  const [loadingTender, setLoadingTender] = useState(false);
  const [formData, setFormData] = useState({});
  const [approvedTenderIdsWithTitle, setApprovedTenderIdsWithTitle] = useState(
    []
  );
  const [vendorName, setVendorName] = useState("");
  const [vendorList, setVendorList] = useState([]);
  const token = useSelector((state) => state.auth?.token);
  const [isUploading, setIsUploading] = useState(false);
  const role = useSelector((state) => state.auth.role);
  const [loadingRows, setLoadingRows] = useState({});
  const [savingAll, setSavingAll] = useState(false);
  const [allVendorVisible, setAllVendorVisible] = useState(false);
  const [tenderInitiated, setTenderInitiated] = useState(false);
  const uploadAbortRefs = useRef({});

  // Fetch approved tenders
  useEffect(() => {
    const fetchApprovedTenders = async () => {
      try {
        setLoadingTender(true);
        const response = await axios.get(
          "/api/tender-requests/approvedGemTender/TenderEvaluation",
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setApprovedTenderIdsWithTitle(response.data.responseData || []);
      } catch (error) {
        console.error("Error fetching approved tenders:", error);
        message.error("Failed to load approved tenders");
      } finally {
        setLoadingTender(false);
      }
    };
    fetchApprovedTenders();
  }, [token]);

  // Fetch vendors for selected tender
  useEffect(() => {
    const fetchVendors = async () => {
      if (!formData.tenderId) return;
      try {
        const tenderId = formData.tenderId;
        setLoadingTender(true);
        const res = await axios.get(
          `/api/vendor-quotation`,
          {
            params: { userRole: role  , tenderId: tenderId},
          }
        );

        const vendorData = res.data?.responseData || [];
        setVendorList(
          vendorData.map((v) => ({
            vendorId: v.vendorId,
            vendorName: v.vendorName,
            status: v.status || "SUBMITTED",
            technicalBidFile: null,
            priceBidFile: null,
            clarificationFileName: v.clarificationFileName || null,
            vendorResponse: v.vendorResponse || "",
            remarks: v.remarks || "",
            indentorStatus:v.indentorStatus || '',
          }))
        );
      } catch (err) {
        console.error("Error fetching vendors:", err);
        setVendorList([]); // no vendors
      } finally {
        setLoadingTender(false);
      }
    };
    fetchVendors();
  }, [formData.tenderId, token, role]);

  const handleChange = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  // const handleFileChange = (vendorName, docType, fileData) => {
  //   setVendorList((prevList) =>
  //     prevList.map((vendor) => {
  //       if (vendor.vendorName === vendorName) {
  //         if (fileData === null) {
  //           return { ...vendor, [docType]: null };
  //         }
  //         const payload = {
  //           file: fileData.file.originFileObj,
  //           originalName: fileData.file.name,
  //         };
  //         return { ...vendor, [docType]: payload };
  //       }
  //       return vendor;
  //     })
  //   );
  // };


// NEW lines 95–111
const handleFileChange = async (vendorName, docType, fileData) => {
  const abortKey = `${vendorName}_${docType}`;

  if (fileData === null) {
    // abort in-flight upload if running
    uploadAbortRefs.current[abortKey]?.abort();
    delete uploadAbortRefs.current[abortKey];

    // delete already-uploaded file from server
    const existing = vendorList.find((v) => v.vendorName === vendorName);
    const uploadedName = existing?.[`${docType}UploadedName`];
    if (uploadedName) {
      axios.delete(`/file/delete`, {
        params: { fileName: uploadedName, fileType: "Tender" },
        headers: { Authorization: `Bearer ${token}` },
      }).catch((err) => console.warn("Cleanup failed:", uploadedName, err));
    }

    setVendorList((prev) =>
      prev.map((v) =>
        v.vendorName === vendorName
          ? { ...v, [docType]: null, [`${docType}UploadedName`]: null, [`${docType}Uploading`]: false }
          : v
      )
    );
    return;
  }

  const payload = {
    file: fileData.file.originFileObj,
    originalName: fileData.file.name,
  };

  // show file selected + uploading spinner
  setVendorList((prev) =>
    prev.map((v) =>
      v.vendorName === vendorName
        ? { ...v, [docType]: payload, [`${docType}UploadedName`]: null, [`${docType}Uploading`]: true }
        : v
    )
  );

  // eager upload
  const controller = new AbortController();
  uploadAbortRefs.current[abortKey] = controller;

  try {
    const fd = new FormData();
    fd.append("file", payload.file);
    const resp = await axios.post("/file/upload?fileType=Tender", fd, {
      signal: controller.signal,
      headers: {
        "Content-Type": "multipart/form-data",
        Accept: "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    const uploadedName = resp.data.responseData.fileName;
    delete uploadAbortRefs.current[abortKey];

    setVendorList((prev) =>
      prev.map((v) =>
        v.vendorName === vendorName
          ? { ...v, [`${docType}UploadedName`]: uploadedName, [`${docType}Uploading`]: false }
          : v
      )
    );
  } catch (err) {
    if (axios.isCancel(err) || err.name === "CanceledError") return; // aborted = user deselected, ignore
    console.error(`Upload failed — Vendor: "${vendorName}" | File: "${docType}" (${payload.originalName})`, err);
    message.error(`Failed to upload ${docType} for ${vendorName} (${payload.originalName})`);
    setVendorList((prev) =>
      prev.map((v) =>
        v.vendorName === vendorName
          ? { ...v, [docType]: null, [`${docType}UploadedName`]: null, [`${docType}Uploading`]: false }
          : v
      )
    );
  }
};

  const handleInputChange = (vendorId, field, value) => {
    setVendorList((prev) =>
      prev.map((item) =>
        item.vendorId === vendorId ? { ...item, [field]: value } : item
      )
    );
  };
  // Fetch vendors for selected tender
const fetchVendors = async (tenderId) => {
  if (!tenderId) return;
  try {
    setLoadingTender(true);
    const res = await axios.get(
      `/api/vendor-quotation/${tenderId}`,
      { params: { userRole: role } }
    );

    const vendorData = res.data?.responseData || [];
    setVendorList(
      vendorData.map((v) => ({
        vendorId: v.vendorId,
        vendorName: v.vendorName,
        status: v.status || "SUBMITTED",
        technicalBidFile: null,
        priceBidFile: null,
        clarificationFileName: v.clarificationFileName || null,
        vendorResponse: v.vendorResponse || "",
        remarks:v.remarks || "",
        indentorStatus:v.indentorStatus || '',
      }))
    );
  } catch (err) {
    console.error("Error fetching vendors:", err);
    setVendorList([]);
  } finally {
    setLoadingTender(false);
  }
};

useEffect(() => {
  const checkInitiation = async () => {
    if (!formData.tenderId) {
      setTenderInitiated(false);
      return;
    }
    try {
      const res = await axios.get("/api/tender-evaluation/status", {
        params: { tenderId: formData.tenderId },
      });
      const evalStatus = res.data?.responseData?.evaluationStatus;
      setTenderInitiated(!!evalStatus);
    } catch {
      setTenderInitiated(false);
    }
  };
  checkInitiation();
}, [formData.tenderId]);



  // Vendor Table Columns
  const columns = [
    {
      title: "Sl. No",
      key: "index",
      render: (_, __, index) => index + 1,
    },
    {
      title: "Vendor Name",
      dataIndex: "vendorName",
      key: "vendorName",
    },
    {
      title: "VendorId",
      dataIndex: "vendorId",
      key: "vendorId",
    },
    {
      title: "Upload Technical Bid",
      key: "uploadTechnical",
      render: (_, record) =>
        record.status === "NEW" ? (
          <FileUploadVendor
            fileType="document"
            onChange={(fileData) =>
              handleFileChange(record.vendorName, "technicalBidFile", fileData)
            }
            fileName={
              record.technicalBidFile
                ? record.technicalBidFile.originalName
                : "No file selected"
            }
            value={
              record.technicalBidFile
                ? { file: { ...record.technicalBidFile } }
                : null
            }
          />
        ) : null,
    },
    {
      title: "Upload Price Bid",
      key: "uploadPrice",
      render: (_, record) =>
        record.status === "NEW" ? (
          <FileUploadVendor
            fileType="document"
            onChange={(fileData) =>
              handleFileChange(record.vendorName, "priceBidFile", fileData)
            }
            fileName={
              record.priceBidFile
                ? record.priceBidFile.originalName
                : "No file selected"
            }
            value={
              record.priceBidFile
                ? { file: { ...record.priceBidFile } }
                : null
            }
          />
        ) : null,
    },
{
  title: "Clarification File",
  key: "clarificationFile",
  render: (_, record) =>
    record.indentorStatus === "CHANGE_REQUESTED" ? (
      <FileUploadVendor
        fileType="document"
        onChange={(fileData) =>
          handleFileChange(record.vendorName, "clarificationFile", fileData)
        }
        fileName={
          record.clarificationFile
            ? record.clarificationFile.originalName
            : "No file selected"
        }
        value={
          record.clarificationFile
            ? { file: { ...record.clarificationFile } }
            : null
        }
      />
    ) : record.clarificationFile?.originalName || "-"
},

{
  title: "Clarification Response",
  dataIndex: "vendorResponse",
  key: "vendorResponse",
  render: (_, record) =>
    record.indentorStatus === "CHANGE_REQUESTED" ? (
      <Input.TextArea
        rows={2}
        placeholder="Enter response"
        value={record.vendorResponse} // controlled input
        onChange={(e) =>
          handleInputChange(record.vendorId, "vendorResponse", e.target.value)
        }
      />
    ) : (
      record.vendorResponse || "-"
    )
},

{
  title: 'Clarification Sought',
  key: 'remarks',
  dataIndex: 'remarks',
  render: (text, record) => {
    console.log("Remarks column data:", record); 
     if (record.indentorStatus !== "CHANGE_REQUESTED") {
      return null;
    }
    return text || '';
  },
     },
{
  title: 'Status',
  key: 'status',
  dataIndex: 'status',
  render: (status) => {
    if (status === 'CHANGE_REQUESTED') return 'Pending Clarification';
    if (status === 'PENDING_SPO') return 'In Progress';
    if (status === 'Completed') return 'Qualified';
    if (status === 'REJECTED') return 'Disqualified';
    return status || 'N/A';
  },
},
  
{
  title: "Action",
  key: "action",
  render: (_, record) => {
    if (record.status !== "NEW" && record.indentorStatus !== "CHANGE_REQUESTED") {
      return null;
    }

    return (
      <div
        className="custom-btn"
        style={{ display: "flex", gap: "10px", marginTop: 16 }}
      >
        <Btn
          onClick={() => handleSubmit(record)}
          loading={loadingRows[record.vendorId || record.vendorName] || false}
        >
          {record.status === "CHANGE_REQUESTED"
            ? "Send Clarification Response"
            : "Send Quotation for Evaluation"}
        </Btn>
      </div>
    );
  },
}


  ];
/*
  const handleSubmit = async (record) => {
    if (!record.technicalBidFile) {
      message.warning("Please upload the technical bid file");
      return;
    }
    if (!record.priceBidFile) {
      message.warning("Please upload the price bid file");
      return;
    }

    setIsUploading(true);
    try {
      const upload = async (fileObj) => {
        const fd = new FormData();
        fd.append("file", fileObj.file);
        const resp = await axios.post("/file/upload?fileType=Tender", fd, {
          headers: {
            "Content-Type": "multipart/form-data",
            Accept: "application/json",
          },
        });
        return resp.data.responseData.fileName;
      };

      let technicalBidFileName = await upload(record.technicalBidFile);
      let priceBidFileName = await upload(record.priceBidFile);
    let clarificationFileName = null;
    if (actionStatus === 'CHANGE_REQUESTED') {
     clarificationFileName = await upload(clarificationFile);
    }

      const quotationBody = {
        tenderId: formData.tenderId,
        vendorName: record.vendorName,
        quotationFileName: technicalBidFileName,
        priceBidFileName,
        fileType: "Tender",
        type: "GEM",
         ...(actionStatus === 'CHANGE_REQUESTED' && {
        clarificationFileName,
        vendorResponse:clarificationResponse,
        status:"Change Requested",
        }),
        createdBy: null,
      };

      const response = await axios.post("/api/vendor-quotation", quotationBody, {
        headers: { "Content-Type": "application/json" },
      });

      if (response.data.responseStatus.statusCode === 0) {
        message.success(
          `Quotation submitted successfully for ${record.vendorName}`
        );
      } else {
        throw new Error("Failed to submit quotation");
      }
    } catch (error) {
      console.error("Submission error:", error);
      message.error("An error occurred while submitting your quotation");
    } finally {
      setIsUploading(false);
    }
  };*/
  const handleSubmit = async (record) => {
    if (tenderInitiated) {
      message.error("Tender already under evaluation. Cannot submit new quotations.");
      return;
    }
    const rowKey = record.vendorId || record.vendorName;
  // When NEW -> normal quotation
  if (record.status === "NEW") {
    if (!record.technicalBidFile) {
      message.warning("Please upload the technical bid file");
      return;
    }
    if (!record.priceBidFile) {
      message.warning("Please upload the price bid file");
      return;
    }
  }

  // When CHANGE_REQUESTED -> clarification
  if (record.status === "CHANGE_REQUESTED") {
    if (!record.clarificationFile) {
      message.warning("Please upload a clarification file");
      return;
    }
    if (!record.vendorResponse) {
      message.warning("Please enter a clarification response");
      return;
    }
  }

  setIsUploading(true);
  try {
    const upload = async (fileObj) => {
      const fd = new FormData();
      fd.append("file", fileObj.file);
      const resp = await axios.post("/file/upload?fileType=Tender", fd, {
        headers: {
          "Content-Type": "multipart/form-data",
          Accept: "application/json",
        },
      });
      return resp.data.responseData.fileName;
    };

    let technicalBidFileName = record.technicalBidFile
      ? await upload(record.technicalBidFile)
      : null;
    let priceBidFileName = record.priceBidFile
      ? await upload(record.priceBidFile)
      : null;
    let clarificationFileName = record.clarificationFile
      ? await upload(record.clarificationFile)
      : null;

    const quotationBody = {
      tenderId: formData.tenderId,
      vendorName: record.vendorName,
      quotationFileName: technicalBidFileName,
      priceBidFileName,
      fileType: "Tender",
      ...( record.status === "NEW" && {type: "GEM"}),
      ...(record.status === "CHANGE_REQUESTED" && {
        clarificationFileName,
        vendorResponse: record.vendorResponse,
        status:"Change Requested",
        vendorId:record.vendorId,
        indentorStatus:record.indentorStatus,
      }),
      createdBy: null,
    };

    const response = await axios.post("/api/vendor-quotation", quotationBody, {
      headers: { "Content-Type": "application/json" },
    });

    if (response.data.responseStatus.statusCode === 0) {
      message.success(
        record.status === "CHANGE_REQUESTED"
          ? `Clarification submitted for ${record.vendorName}`
          : `Quotation submitted successfully for ${record.vendorName}`
      );
      await fetchVendors(formData.tenderId);
    } else {
      throw new Error("Failed to submit");
    }
  } catch (error) {
    console.error("Submission error:", error);
    message.error("An error occurred while submitting");
  } finally {
    setIsUploading(false);
    setLoadingRows((prev) => ({ ...prev, [rowKey]: false }));
  }
};

const handleSubmitAll = async () => {
  if (tenderInitiated) {
    message.error("Tender already under evaluation. Cannot submit new quotations.");
    return;
  }
  const newRows = vendorList.filter((v) => v.status === "NEW");

  if (newRows.length === 0) {
    message.warning("No new vendors to submit");
    return;
  }

  // const incomplete = newRows.filter(
  //   (v) => !v.technicalBidFile || !v.priceBidFile
  // );
  // if (incomplete.length > 0) {
  //   message.error(
  //     `Please upload both files for: ${incomplete
  //       .map((v) => v.vendorName)
  //       .join(", ")}`
  //   );
  //   return;
  // }


// NEW
const stillUploading = newRows.filter(
  (v) => v.technicalBidFileUploading || v.priceBidFileUploading
);
if (stillUploading.length > 0) {
  message.warning(`Still uploading, please wait: ${stillUploading.map((v) => v.vendorName).join(", ")}`);
  return;
}
const incomplete = newRows.filter(
  (v) => !v.technicalBidFileUploadedName || !v.priceBidFileUploadedName
);
if (incomplete.length > 0) {
  message.error(`Files missing or upload failed for: ${incomplete.map((v) => v.vendorName).join(", ")}`);
  return;
}
  setSavingAll(true);
  try {
  const uploadResults = newRows.map((row) => ({
    vendorName: row.vendorName,
    quotationFileName: row.technicalBidFileUploadedName,
    priceBidFileName: row.priceBidFileUploadedName,
    fileType: "Tender",
    type: "GEM",
  }));
//   try {
// const upload = async (fileObj, vendorName, fileLabel) => {
//   const fd = new FormData();
//   fd.append("file", fileObj.file);
//   try {
//     const resp = await axios.post("/file/upload?fileType=Tender", fd, {
//       headers: {
//         "Content-Type": "multipart/form-data",
//         Accept: "application/json",
//       },
//     });
//     return resp.data.responseData.fileName;
//   } catch (err) {
//     const enriched = new Error(
//       `Upload failed — Vendor: "${vendorName}" | File: "${fileLabel}" (${fileObj.originalName})`
//     );
//     enriched.originalError = err;
//     throw enriched;
//   }
// };

// const uploadResults = await Promise.all(
//   newRows.map(async (row) => {
//     const techFileName = await upload(row.technicalBidFile, row.vendorName, "Technical Bid");
//     const priceFileName = await upload(row.priceBidFile, row.vendorName, "Price Bid");
//     return {
//       vendorName: row.vendorName,
//       quotationFileName: techFileName,
//       priceBidFileName: priceFileName,
//       fileType: "Tender",
//       type: "GEM",
//     };
//   })
// );
    const response = await axios.post("/api/vendor-quotation/bulk", {
      tenderId: formData.tenderId,
      quotations: uploadResults,
    });

    if (response.data.responseStatus.statusCode === 0) {
      message.success(
        `All ${uploadResults.length} quotations submitted successfully`
      );
      await fetchVendors(formData.tenderId);
    } else {
      throw new Error("Bulk save failed");
    }
    } catch (error) {
  console.error("Bulk submission error:", error.message, error.originalError || "");
  message.error(error.message || "An error occurred while submitting quotations");
} finally{
  // } catch (error) {
  //   console.error("Bulk submission error:", error);
  //   message.error("An error occurred while submitting quotations");
  // } finally {
    setSavingAll(false);
  }
};

  const handleClarificationSubmit = async (record) => {
    if (!record.clarificationFileName) {
      message.warning("Please upload a clarification file");
      return;
    }
    if (!record.vendorResponse) {
      message.warning("Please enter a response");
      return;
    }

    setIsUploading(true);
    try {
      const upload = async (fileObj) => {
        const fd = new FormData();
        fd.append("file", fileObj.file);
        const resp = await axios.post("/file/upload?fileType=Tender", fd, {
          headers: {
            "Content-Type": "multipart/form-data",
            Accept: "application/json",
          },
        });
        return resp.data.responseData.fileName;
      };

      let clarificationFileName = await upload(record.clarificationFileName);

      const clarificationBody = {
        tenderId: formData.tenderId,
        vendorId: record.vendorId,
        clarificationFileName,
        vendorResponse: record.vendorResponse,
      };

      const response = await axios.post(
        "/api/vendor-quotation/clarification",
        clarificationBody,
        { headers: { "Content-Type": "application/json" } }
      );

      if (response.data.responseStatus.statusCode === 0) {
        message.success(`Clarification submitted for ${record.vendorName}`);
      } else {
        throw new Error("Failed to submit clarification");
      }
    } catch (error) {
      console.error("Clarification error:", error);
      message.error("An error occurred while submitting clarification");
    } finally {
      setIsUploading(false);
    }
  };

  const handleAdd = () => {
    if (!vendorName.trim()) {
      return message.warning("Please enter a vendor name");
    }
    if (
      vendorList.some(
        (v) =>
          v.vendorName &&
          vendorName &&
          v.vendorName.toLowerCase() === vendorName.toLowerCase()
      )
    ) {
      return message.error("Vendor already exists");
    }

    setVendorList([
      ...vendorList,
      {
        vendorId: null,
        vendorName,
        status: "NEW",
        technicalBidFile: null,
        priceBidFile: null,
      },
    ]);
    setVendorName("");
  };

  const onFinish = () => {
    if (!formData.tenderId) return message.error("Tender ID required");
    if (!vendorList.length) return message.error("No vendors added");
    message.success("Evaluation submitted");
  };

  // TenderId dropdown
  const TenderDetails = [
    {
      heading: "Tender Search",
      colCnt: 1,
      fieldList: [
        {
          name: "tenderId",
          label: "Tender Id",
          type: "selectTenderId",
          span: 1,
          options: approvedTenderIdsWithTitle.map((item) => ({
            label: item.tenderId + " - " + item.title,
            value: item.tenderId,
          })),
          onChange: (selectedValue) => {
            handleChange("tenderId", selectedValue);
          },
        },
      ],
    },
  ];

  return (
    <FormContainer>
      <Heading
        title={
          loadingTender
            ? "Loading..."
            : `GEM Tender Evaluation for Tender ID: ${formData.tenderId || "-"}`
        }
      />

      <CustomForm
        formData={formData}
        onFinish={onFinish}
        onFinishFailed={() => message.error("Please check required fields")}
      >
        {renderFormFields(
          TenderDetails,
          handleChange,
          formData,
          "",
          null,
          setFormData,
          null
        )}

        {/* Vendor Section */}
        <FormBody layout="vertical" style={{ marginTop: 16 }}>
          <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
            <Input
              placeholder="Enter Vendor Name"
              value={vendorName}
              onChange={(e) => setVendorName(e.target.value)}
              style={{ width: 300 }}
            />
            <Button type="primary" onClick={handleAdd}>
              Add Vendor
            </Button>
          </div>
          <div>
           <Button
                type="link"
                icon={<HistoryOutlined />}
                onClick={() => setAllVendorVisible(true)}
                style={{ marginTop: 20, marginLeft: 10 }}
            >
             View All Vendors Status
            </Button>
             {/* New All Vendors Status Modal */}
            <AllVendorsQuotationsstatus
      open={allVendorVisible}
      onClose={() => setAllVendorVisible(false)}
      tenderId={formData.tenderId}
     
    />
</div>
          {loadingTender && <Spin tip="Loading..." style={{ marginBottom: 12 }} />}

          <Table
            dataSource={vendorList}
            columns={columns}
            rowKey="vendorName"
            pagination={false}
          />

          {vendorList.some((v) => v.status === "NEW") && (
            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 16 }}>
              <Button
                type="primary"
                onClick={handleSubmitAll}
                loading={savingAll}
                disabled={isUploading}
              >
                Send All Quotations for Evaluation
              </Button>
            </div>
          )}
        </FormBody>
      </CustomForm>
    </FormContainer>
  );
};

export default TenderEvaluatorGem;
