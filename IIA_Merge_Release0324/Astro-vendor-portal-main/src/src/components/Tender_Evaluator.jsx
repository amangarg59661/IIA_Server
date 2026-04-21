import React, { useState, useEffect} from 'react';
import axios from 'axios';
//import { message } from 'antd';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import FileUpload from './DKG_FileUpload';
import FormContainer from './DKG_FormContainer';
import Heading from './DKG_Heading';
import Btn from './DKG_Btn';
import { Form, Upload, Button, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import QueueModal from "./QueueModal";
import { Modal } from "antd";
import QuotationHistoryModal from './QuotationHistoryModal';
import { HistoryOutlined } from '@ant-design/icons';

import { baseURL } from '../App';

//const TenderEvaluator = ({ tenderId }) => {
const TenderEvaluator = ({ tenderId, actionStatus }) => {

  //const { userId } = useSelector(state => state.auth);
  const vendorId = useSelector((state) => state.auth.vendorId);
  console.log("Vendor ID from Redux:", vendorId);
  
  const navigate = useNavigate();

  const [quotationFile, setQuotationFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [closingDate, setClosingDate] = useState(null);
  const [isAfterClosing, setIsAfterClosing] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [detailsData, setDetailsData] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [showPopup, setShowPopup] = useState(false);
  const [historyVisible, setHistoryVisible] = useState(false);
 // const [quotationFile, setQuotationFile] = useState(null);
  const [priceBidFile, setPriceBidFile] = useState(null);
  const [bidType, setBidType] = useState(''); // new
  const [clarificationFile, setClarificationFile] = useState(null);
  const [clarificationResponse, setClarificationResponse] = useState('');






  // ─── NEW EFFECT: FETCH AND COMPARE CLOSING DATE ──────────────────────────────
 useEffect(() => {
  axios
    .get(`/api/tender-requests/data/${tenderId}`)
    .then(res => {
      const cdString = res.data.responseData.closingDate; // e.g., "10/05/2025"
      if (!cdString) return;

      // Parse "dd/MM/yyyy"
      const [day, month, year] = cdString.split('/').map(Number);
      const closingDate = new Date(year, month - 1, day, 23, 59, 59, 999); // end of day

      const now = new Date();
      setBidType(res.data.responseData.bidType || '');

      setClosingDate(closingDate);
      setIsAfterClosing(now > closingDate); // true = too late
    })
    .catch(err => {
      console.error('Failed to fetch closing date:', err);
    });
}, [tenderId]);


/*
  const handleFileChange = (docName, fileData) => {
    if (fileData === null) {
      setQuotationFile(null);
    } else {
      setQuotationFile({
        file: fileData.file.originFileObj,
        originalName: fileData.file.name
      });
    }
  };*//*
  const handleFileChange = (docName, fileData) => {
  if (fileData === null) {
    if (docName === 'quotationUpload') setQuotationFile(null);
    if (docName === 'priceBid') setPriceBidFile(null);
  } else {
    const payload = {
      file: fileData.file.originFileObj,
      originalName: fileData.file.name
    };
    if (docName === 'quotationUpload') {
      setQuotationFile({ file: payload.file, originalName: payload.originalName });
    }
    if (docName === 'priceBid') {
      setPriceBidFile({ file: payload.file, originalName: payload.originalName });
    }
    
  }
};*/
const handleFileChange = (docName, fileData) => {
  if (fileData === null) {
    if (docName === 'quotationUpload') setQuotationFile(null);
    if (docName === 'priceBid') setPriceBidFile(null);
    if (docName === 'clarificationUpload') setClarificationFile(null);
  } else {
    const payload = {
      file: fileData.file.originFileObj,
      originalName: fileData.file.name
    };

    if (docName === 'quotationUpload') {
      setQuotationFile({ file: payload.file, originalName: payload.originalName });
    }
    if (docName === 'priceBid') {
      setPriceBidFile({ file: payload.file, originalName: payload.originalName });
    }
    if (docName === 'clarificationUpload') {
      setClarificationFile({ file: payload.file, originalName: payload.originalName });
    }
  }
};


/*
  const handleSubmit = async () => {
    if (!quotationFile) {
      message.warning('Please upload a quotation file');
      return;
    }

    setIsUploading(true);

    try {
      // Upload file
      const formData = new FormData();
      formData.append('file', quotationFile.file);

      const fileUploadResponse = await axios.post(
        '/file/upload?fileType=Tender',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Accept': 'application/json'
          }
        }
      );

      const serverFileName = fileUploadResponse.data.responseData.fileName;

      // Now call the API to submit quotation details
      const quotationBody = {
        tenderId: tenderId,
        vendorId: vendorId,  // assuming vendorId = userId or you can try with V1001
        quotationFileName: serverFileName,
        fileType: "Tender",
        createdBy: null
      };

      const response = await axios.post(
        '/api/vendor-quotation',
        quotationBody,
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      const { responseStatus, responseData } = response.data;

      if (responseStatus.statusCode === 0) {
        message.success('Quotation submitted successfully');
      //  navigate('/purchaseOrder');
      /* setTimeout(() => {
         // setSuccessMessage('');
          navigate('/');
        }, 1000);*/
       /* setSuccessMessage("Vendor quotation submitted successfully.");
        setQuotationFile(null);   
        setShowPopup(true);   
      } else {
        throw new Error('Failed to submit quotation');
      }

    } catch (error) {
      console.error('Submission error:', error);
      message.error('An error occurred while submitting your quotation');
    } finally {
      setIsUploading(false);
    }
  };*/
  const handleSubmit = async () => {
    console.log("its calling");

  if (actionStatus === 'CHANGE_REQUESTED') {
  if (!clarificationFile) {
    message.warning('Please upload clarification document');
    return;
  }
  if (!clarificationResponse) {
    message.warning('Please enter clarification response');
    return;
  }
}else{
   if (!quotationFile) {
    message.warning('Please upload a quotation file');
    return;
  }
 /* if (bidType === 'Double' && !priceBidFile) {
    message.warning('Please upload the price bid file');
    return;
  }*/

  if (!priceBidFile) {
    message.warning('Please upload the price bid file');
    return;
  }

 
}


  setIsUploading(true);
  try {
    // upload quotation
    const upload = async (fileObj) => {
      const fd = new FormData();
      fd.append('file', fileObj.file);
      const resp = await axios.post('/file/upload?fileType=Tender', fd, {
        headers: { 'Content-Type': 'multipart/form-data', Accept: 'application/json' },
      });
      return resp.data.responseData.fileName;
    };

   let quotationFileName =null;
    let priceBidFileName = null;
   /* if (bidType === 'Double') {
      priceBidFileName = await upload(priceBidFile);
    }*/
    if (priceBidFile) {
      priceBidFileName = await upload(priceBidFile);
    }

    let clarificationFileName = null;
    if (actionStatus === 'CHANGE_REQUESTED') {
     clarificationFileName = await upload(clarificationFile);
    }else{
         quotationFileName = await upload(quotationFile);
    }


    const quotationBody = {
      tenderId: tenderId,
      vendorId: vendorId,
      quotationFileName: quotationFileName,
      fileType: 'Tender',
      createdBy: null,
    //  ...(bidType === 'Double' && { priceBidFileName }), // include if double
    ...((bidType === 'Double' || bidType === 'Single') && { priceBidFileName }),
    ...(actionStatus === 'CHANGE_REQUESTED' && {
    clarificationFileName,
    vendorResponse:clarificationResponse,
    status:"Change Requested",
  }),
    };

    const response = await axios.post('/api/vendor-quotation', quotationBody, {
      headers: { 'Content-Type': 'application/json' },
    });

    const { responseStatus } = response.data;

    if (responseStatus.statusCode === 0) {
      message.success('Quotation submitted successfully');
      setQuotationFile(null);
      setPriceBidFile(null);
      setShowPopup(true);
    } else {
      throw new Error('Failed to submit quotation');
    }
  } catch (error) {
    console.error('Submission error:', error);
    message.error('An error occurred while submitting your quotation');
  } finally {
    setIsUploading(false);
  }
};


/*
  return (
    <FormContainer>
      <Heading title={`Upload Quotation for Tender ID: ${tenderId}`} />

      <FileUpload
        documentName="Upload Quotation for Evaluation"
        fileType="document"
        onChange={(fileData) => handleFileChange("quotationUpload", fileData)}
        fileName={quotationFile ? quotationFile.originalName : "No file selected"}
        value={quotationFile ? { file: { ...quotationFile } } : null}
      />
       
      <div className="custom-btn" style={{ display: 'flex', gap: '10px' }}>
        <Btn onClick={handleSubmit} loading={isUploading}>
          Send Quotation for Evaluation
        </Btn>
      </div>
    </FormContainer>
  );*/
  const handleOpenTenderFormat = () => {
  const url = `${baseURL}/data/tender-format?tenderId=${tenderId}&vendorId=${vendorId}`;
  window.open(url, "_blank");
};

  

  return (
    <FormContainer>
       <Modal
      title="Success"
      open={showPopup}
      onOk={() => setShowPopup(false)}
      onCancel={() => setShowPopup(false)}
      okText="OK"
      cancelButtonProps={{ style: { display: "none" } }}
    >
      <p>Vendor quotation submitted successfully.</p>
    </Modal>
      <Heading title={`Upload Quotation for Tender ID: ${tenderId}`} />

       <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
  <p style={{ fontWeight: "bold", marginBottom: 0 }}>
    Please click on Tender ID to see tender details:{" "}
    <span
      style={{
        color: "#1890ff",
        cursor: "pointer",
        textDecoration: "underline",
      }}
      onClick={async () => {
        setModalVisible(true);
        setDetailsData(null);
        setSelectedRecord({ requestId: tenderId, workflowId: 4 });

        try {
          const response = await axios.get(`/api/tender-requests/${tenderId}`);
          setDetailsData(response.data.responseData);
        } catch (err) {
          console.error("Failed to fetch tender details:", err);
          message.error("Could not load tender details");
          setModalVisible(false);
        }
      }}
    >
      {tenderId}
    </span>
  </p>
  <Button onClick={handleOpenTenderFormat}>View Tender Copy</Button>

{/*
  <Button
    type="link"
    icon={<HistoryOutlined />}
    onClick={() => setHistoryVisible(true)}
  >
    View Quotation History
  </Button>*/}
</div>




  { /*  
     {closingDate && (
  <p style={{ fontWeight: 'bold' }}>
    Closing Date: {closingDate.toLocaleDateString()}
  </p>
)}

{isAfterClosing ? (
  <p style={{ color: 'red', fontWeight: 'bold' }}>
    Tender closing date is completed. Not allowed to upload Quotation document.
  </p>
) : (
  <>
  {actionStatus === "CHANGE_REQUESTED" ? (
    <>
      <FileUpload
        documentName="Upload Clarification Document"
        fileType="document"
         onChange={fileData => handleFileChange('clarificationUpload', fileData)} 
        fileName={clarificationFile ? clarificationFile.originalName : 'No file selected'}
        value={clarificationFile ? { file: { ...clarificationFile } } : null}
      />
      <div style={{ marginTop: 16 }}>
        <label><b>Clarification Response:</b></label>
        <textarea
          rows={4}
          style={{ width: '100%', marginTop: 8 }}
          placeholder="Enter clarification response"
          value={clarificationResponse}
          onChange={e => setClarificationResponse(e.target.value)}
        />
      </div>
    </>
  ) : (
    <>
      <FileUpload
        documentName="Upload Technical Bid"
        fileType="document"
        onChange={fileData => handleFileChange('quotationUpload', fileData)}
        fileName={quotationFile ? quotationFile.originalName : 'No file selected'}
        value={quotationFile ? { file: { ...quotationFile } } : null}
      />
      {bidType === 'Double' && (
        <div style={{ marginTop: 12 }}>
          <FileUpload
            documentName="Upload Price Bid"
            fileType="document"
            onChange={fileData => handleFileChange('priceBid', fileData)}
            fileName={priceBidFile ? priceBidFile.originalName : 'No file selected'}
            value={priceBidFile ? { file: { ...priceBidFile } } : null}
          />
        </div>
      )}
    </>
  )}

     <div className="custom-btn" style={{ display: 'flex', gap: '10px', marginTop: 16 }}>
      <Btn onClick={handleSubmit} loading={isUploading}>
        {actionStatus === "CHANGE_REQUESTED"
          ? "Send Clarification Response"
          : "Send Quotation for Evaluation"}
      </Btn>
    </div>

</>


)}
*/}
 {closingDate && (
  <p style={{ fontWeight: 'bold' }}>
    Closing Date: {closingDate.toLocaleDateString()}
  </p>
)}
{isAfterClosing && actionStatus !== "CHANGE_REQUESTED" ? (
  <p style={{ color: 'red', fontWeight: 'bold' }}>
    Tender closing date is completed. Not allowed to upload Quotation document.
  </p>
) : (
  <>
    {actionStatus === "CHANGE_REQUESTED" ? (
      <>
        <FileUpload
          documentName="Upload Clarification Document"
          fileType="document"
          onChange={fileData => handleFileChange('clarificationUpload', fileData)}
          fileName={clarificationFile ? clarificationFile.originalName : 'No file selected'}
          value={clarificationFile ? { file: { ...clarificationFile } } : null}
        />
        <div style={{ marginTop: 16 }}>
          <label><b>Clarification Response:</b></label>
          <textarea
            rows={4}
            style={{ width: '100%', marginTop: 8 }}
            placeholder="Enter clarification response"
            value={clarificationResponse}
            onChange={e => setClarificationResponse(e.target.value)}
          />
        </div>
      </>
    ) : (
      <>
        <FileUpload
          documentName="Upload Technical Bid"
          fileType="document"
          onChange={fileData => handleFileChange('quotationUpload', fileData)}
          fileName={quotationFile ? quotationFile.originalName : 'No file selected'}
          value={quotationFile ? { file: { ...quotationFile } } : null}
        />
        {//bidType === 'Double' && (
          <div style={{ marginTop: 12 }}>
            <FileUpload
              documentName="Upload Price Bid"
              fileType="document"
              onChange={fileData => handleFileChange('priceBid', fileData)}
              fileName={priceBidFile ? priceBidFile.originalName : 'No file selected'}
              value={priceBidFile ? { file: { ...priceBidFile } } : null}
            />
          </div>
       // )
       }
      </>
    )}

    <div className="custom-btn" style={{ display: 'flex', gap: '10px', marginTop: 16 }}>
      <Btn onClick={handleSubmit} loading={isUploading}>
        {actionStatus === "CHANGE_REQUESTED"
          ? "Send Clarification Response"
          : "Send Quotation for Evaluation"}
      </Btn>
    </div>
  </>
)}

   <QueueModal
  modalVisible={modalVisible}
  setModalVisible={setModalVisible}
  selectedRecord={selectedRecord}
  detailsData={detailsData}
/>
<QuotationHistoryModal
  open={historyVisible}
  onClose={() => setHistoryVisible(false)}
  tenderId={tenderId}
  vendorId={vendorId}
/>



    </FormContainer>
  );
};

export default TenderEvaluator;