import React, { useEffect, useState} from 'react'
import { Modal, Spin, Tag } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom'
import FormContainer from '../../../components/DKG_FormContainer'
import FormBody from '../../../components/DKG_FormBody'
import Heading from '../../../components/DKG_Heading'
import { Table, Checkbox, message, Popover, Input, Button  } from 'antd'
import axios from 'axios'
import Btn from '../../../components/DKG_Btn'
import { baseURL } from '../../../App';



const Quotations =  ()  => {
 

const location = useLocation();
const { tenderId, bidType } = location.state || {};

 const navigate = useNavigate();
 
  const [quotationData, setQuotationData] = useState([]);
  const [selectedVendors, setSelectedVendors] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [selectedVendor, setSelectedVendor] = useState(null);
  const [notSubmittedVendors, setNotSubmittedVendors] = useState([]);
  const [rejectComment, setRejectComment] = useState('');
  const [rejectedVendorId, setRejectedVendorId] = useState(null);
  const [historyVisible, setHistoryVisible] = useState(false);
  const [selectedVendorForHistory, setSelectedVendorForHistory] = useState(null);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyData, setHistoryData] = useState([]);

  const fetchVendorHistory = async (vendorId) => {
  try {
    setHistoryLoading(true);
    const res = await axios.get(`/api/vendor-quotation/vendorHistory` , {params:{tenderId ,vendorId}});
    setHistoryData(res.data?.responseData || []);
  } catch (error) {
    message.error("Failed to fetch quotation history");
    setHistoryData([]);
  } finally {
    setHistoryLoading(false);
  }
};





  const fetchQuotations = async () => {
    try {
      const response = await axios.get(`/api/vendor-quotation/${tenderId}`);
      const data = response.data?.responseData || [];
      setQuotationData(data);
    } catch (error) {
      message.error('Failed to fetch vendor quotations');
    }
  };

  const fetchNotSubmittedVendors = async () => {
    try {
    const res = await axios.get(`/api/vendor-quotation/NotSubmitVendors`, {params:{tenderId}});
    setNotSubmittedVendors(res.data.responseData || []);
    } catch (error) {
    message.error("Failed to fetch vendors who didn't submit quotations");
    }
  };

 

  const handleCheckboxChange = (vendorId) => {
    setSelectedVendors((prev) =>
      prev.includes(vendorId)
        ? prev.filter((id) => id !== vendorId)
        : [...prev, vendorId]
    );
  };
  //const [tenderData, setTenderData] = useState(null);
/*
const fetchTenderDetails = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/astro-service/api/tender-requests/data/${tenderId}`);
    setTenderData(res.data.responseData);
  } catch (error) {
    message.error("Failed to fetch tender details");
  }
};*/
useEffect(() => {
  if (tenderId) {
    fetchQuotations();
    fetchNotSubmittedVendors();
  }
}, [tenderId]);
/*tenderUpdateDto , fetchTenderDetails();*/
const handleSubmit = async () => {
  if (!selectedVendor) {
    return message.warning("Please select exactly one vendor");
  }

  const selectedQuotation = quotationData.find(q => q.vendorId === selectedVendor);

  const updatedTender = {
    vendorId: selectedVendor,
    quotationFileName: selectedQuotation?.quotationFileName || '',
  };

  try {
    setIsSubmitting(true);
    await axios.put(`/api/tender-requests/update/`, updatedTender, {params: {tenderId}});
    message.success("Tender updated successfully");
    navigate("/queue");
  } catch (error) {
    message.error("Failed to update tender");
  } finally {
    setIsSubmitting(false);
  }
};
const handleAccept = async (record) => {
  try {
    await axios.put(`/api/vendor-quotation/`, null, {
      params: {
        tenderId,
        vendorId: record.vendorId
      }
    });
    message.success(`Vendor ${record.vendorId} accepted`);
    fetchQuotations(); 
  } catch (err) {
    message.error("Failed to accept vendor quotation");
  }
};


  const columns = [
   {
    title: 'Approve',
    key: 'select',
    render: (_, record) => (
    <Checkbox
      checked={selectedVendor === record.vendorId}
      onChange={() => {
        if (selectedVendor === record.vendorId) {
          setSelectedVendor(null); // Toggle off
        } else {
          setSelectedVendor(record.vendorId); // Toggle on
        }
      }}
    />
    ),
  },{
      title: 'Vendor ID',
      dataIndex: 'vendorId',
      key: 'vendorId',
      render: (vendorId) => (
    <a
      style={{ color: '#1890ff' }}
      onClick={() => {
        setSelectedVendorForHistory(vendorId);
        setHistoryVisible(true);
        fetchVendorHistory(vendorId);
      }}
    >
      {vendorId}
    </a>
  ),
    },
    {
      title: 'Quotation File Name',
      dataIndex: 'quotationFileName',
      key: 'quotationFileName',
    },
     {
    title: 'View File',
    key: 'view',
    render: (_, record) => (
      record.quotationFileName ? (
        <a
          href={`${baseURL}/file/view/Tender/${record.quotationFileName}`}
          target="_blank"
          rel="noopener noreferrer"
        >
          View
        </a>
      ) : (
        'No File'
      )
    ),
  },
  {
  title: 'Accept',
  key: 'accept',
  render: (_, record) => (
    record.acceptanceStatus !== 'ACCEPTED' ? (
      <Button
        type="primary"
        onClick={() => handleAccept(record)}
        size="small"
      >
        Accept
      </Button>
    ) : (
      <Tag color="green">Accepted</Tag>
    )
  )
},
  {
    title: 'Reject',
    key: 'reject',
    render: (_, record) => (
      record.status !== 'Rejected' ? (
        <Popover
          content={
            <div style={{ padding: 12 }}>
              <Input.TextArea
                placeholder="Enter reject comment"
                rows={3}
                value={rejectedVendorId === record.vendorId ? rejectComment : ''}
                onChange={(e) => {
                  setRejectedVendorId(record.vendorId);
                  setRejectComment(e.target.value);
                }}
              />
              <Button
                type="primary"
                onClick={() => handleReject(record)}
                style={{ marginTop: 8 }}
              >
                Submit
              </Button>
            </div>
          }
          title="Reject Vendor"
          trigger="click"
        >
          <Button danger type="link">Reject</Button>
        </Popover>
      ) : (
        <span style={{ color: 'red' }}>Rejected</span>
      )
    )
  },
  {
  title: 'Change Request',
  key: 'changeRequest',
  render: (_, record) => (
    record.status !== 'ChangeRequested' ? (
      <Popover
        content={
          <div style={{ padding: 12 }}>
            <Input.TextArea
              placeholder="Enter change request comment"
              rows={3}
              value={rejectedVendorId === record.vendorId ? rejectComment : ''}
              onChange={(e) => {
                setRejectedVendorId(record.vendorId);
                setRejectComment(e.target.value);
              }}
            />
            <Button
              type="primary"
              onClick={() => handleChangeRequest(record)}
              style={{ marginTop: 8 }}
            >
              Submit
            </Button>
          </div>
        }
        title="Send Change Request"
        trigger="click"
      >
        <Button type="link" style={{ color: '#fa8c16' }}>Change Request</Button>
      </Popover>
    ) : (
      <span style={{ color: '#fa8c16' }}>Requested</span>
    )
  )
}

  ];
 const handleChangeRequest = async (record) => {
  if (!rejectComment.trim()) {
    return message.warning("Please enter a change request comment.");
  }

  try {
    await axios.post("/api/vendor-quotation/change-request", {
      tenderId,
      vendorId: record.vendorId,
      remarks: rejectComment
    });
    message.success(`Change request sent to vendor ${record.vendorId}`);
    setRejectComment('');
    setRejectedVendorId(null);
    fetchQuotations(); 
  } catch (err) {
    message.error("Failed to send change request");
  }
};


  const handleReject = async (record) => {
  if (!rejectComment.trim()) {
    return message.warning("Please enter a rejection comment.");
  }

  try {
    await axios.put("/api/vendor-quotation/updateVendorQuotation-status", {
      tenderId,
      vendorId: record.vendorId,
      status: "Rejected",
      remarks: rejectComment
    });
    message.success(`Vendor ${record.vendorId} rejected`);
    setRejectComment('');
    setRejectedVendorId(null);
    fetchQuotations(); // Refresh list
  } catch (err) {
    message.error("Failed to reject vendor");
  }
};

 


  return (
    <FormContainer>
      <Btn onClick={() => navigate('/queue')} className="mb-4">
        Back
      </Btn>
     
      <Heading title={`Quotation Evaluation for Tender ID: ${tenderId} and BidType :${bidType}`} />

       <FormBody layout="vertical">
        {notSubmittedVendors.length > 0 && (
          <div style={{ marginBottom: '1rem', fontWeight: 'bold', }}>
            The following vendors have not submitted quotations: {notSubmittedVendors.join(', ')}
          </div>
        )}
        <Table
          dataSource={quotationData}
          columns={columns}
          rowKey="vendorId"
        />
        <div className="custom-btn" style={{ display: 'flex', gap: '10px' }}>
        <Btn onClick={handleSubmit}  loading={isSubmitting}>Submit Quotation</Btn>
      </div>
      <Modal
  open={historyVisible}
  onCancel={() => setHistoryVisible(false)}
  footer={null}
  width={700}
  title={`Quotation History for Vendor ${selectedVendorForHistory}`}
>
  {historyLoading ? (
    <Spin tip="Loading history..." />
  ) : (
    <Table
      dataSource={historyData}
      rowKey={(record, index) => index}
      bordered
      size="small"
      pagination={false}
      columns={[
        {
          title: 'Status',
          dataIndex: 'status',
          key: 'status',
          render: (text) => (
            <Tag color={
              text === 'Rejected'
                ? 'red'
                : text === 'CHANGE_REQUESTED'
                ? 'orange'
                : 'green'
            }>
              {text}
            </Tag>
          )
        },
        {
          title: 'Remarks',
          dataIndex: 'remarks',
          key: 'remarks',
          render: (text) => text || '--'
        },
        {
          title: 'Date',
          dataIndex: 'date',
          key: 'date',
          render: (text) => text ? new Date(text).toLocaleString('en-IN') : '--'
        }
      ]}
      locale={{ emptyText: 'No quotation history found.' }}
    />
  )}
</Modal>

      </FormBody>
    </FormContainer>
  )
}

export default Quotations
