// import React, { useEffect, useState, useCallback } from 'react';
// import { Table, Button, message, Space, Modal, Input, Popover } from 'antd';
// import axios from 'axios';
// import { useSelector } from "react-redux";
// import { useNavigate } from "react-router-dom"; 
// import InventoryQueueModal from './InventoryQueueModel';
// import { SearchOutlined } from "@ant-design/icons";


// const { TextArea } = Input;
// // Search Input Component
// const FilterComponent = ({ searchTerm, setSearchTerm, onSearch, onReset }) => (
//   <div style={{ marginBottom: 16 }}>
//     <Space>
//       <Input
//         placeholder="Search by Request ID"
//         prefix={<SearchOutlined />}
//         value={searchTerm}
//         onChange={(e) => setSearchTerm(e.target.value)}  //Only update input value
//         style={{ width: 300 }}
//         allowClear
//       />
//       <Button
//         type="primary"
//         icon={<SearchOutlined />}
//         onClick={onSearch} //Search only on button click
//       >
//         Search
//       </Button>
//       <Button onClick={onReset}>Reset</Button>
//     </Space>
//   </div>
// );

// const GiApprovalPage = () => {
//   const [data, setData] = useState([]);
//   const [activePopoverKey, setActivePopoverKey] = useState(null);
//   const [comments, setComments] = useState({});
//   const [modalVisible, setModalVisible] = useState(false);
//   const [selectedRecord, setSelectedRecord] = useState(null);
//   const [loading, setLoading] = useState(false);
//   const [searchTerm, setSearchTerm] = useState("");
//   const [filteredData, setFilteredData] = useState([]);
//   const [rejectComments, setRejectComments] = useState({});
//   const [activeRejectKey, setActiveRejectKey] = useState(null);



//   const auth = useSelector((state) => state.auth);
//   const roleName=auth.role;
//   const navigate = useNavigate(); 
//   console.log(roleName);

//    const fetchData = async () => {
//     try {
//       const url =
//         roleName === 'Store Purchase Officer'
//           ? '/api/process-controller/getGiByStatuses'
//           : '/api/process-controller/getGiByIndentorStatuses';

//       const res = await axios.get(url);
//       setData(res.data.responseData || []);
//       setFilteredData(res.data.responseData || []); 
//     } catch (error) {
//       message.error('Failed to fetch GI list');
//     }
//   };
 
// const handleSearch = () => {
//   if (!searchTerm.trim()) {
//     setFilteredData(data);
//     return;
//   }
//   const lower = searchTerm.toLowerCase();
//   const filtered = data.filter(
//     (item) =>
//       `INV${item.gprnProcessId}/${item.inspectionSubProcessId}`.toLowerCase().includes(lower) ||
//       (item.locationId && item.locationId.toLowerCase().includes(lower))
//   );
//   setFilteredData(filtered);
// };

// // Reset Button
// const handleReset = () => {
//   setSearchTerm("");
//   setFilteredData(data);
// };

//   const handleProcessIdClick = async (record) => {
//   setModalVisible(true);
//   setLoading(true);
//   try {
//     const processNo = `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`;
//     const response = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GI&processNo=${processNo}`);
//     setSelectedRecord(response.data.responseData);
//   } catch (error) {
//     message.error("Failed to fetch process details");
//   } finally {
//     setLoading(false);
//   }
// };


// /*
//   const fetchData = async () => {
//     try {
//       const res = await axios.get('/api/process-controller/getGiByStatuses');
//       setData(res.data.responseData || []);
//     } catch (error) {
//       message.error('Failed to fetch GI list');
//     }
//   };*/

//   useEffect(() => {
//     fetchData();
//   }, []);

//   const rowKey = (record) => `${record.gprnProcessId}_${record.inspectionSubProcessId}`;
// /*
//   const handleApprove = async (record) => {
//     try {
//       await axios.post('/api/process-controller/approveGi', {
//         processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
//       });
//       message.success('GI Approved successfully');
//       fetchData();
//     } catch (error) {
//       message.error(error?.response?.data?.responseStatus?.message || 'Failed to approve GI');
//     }
//   };*/
//   const handleApprove = async (record) => {
//   try {
//     const payload = {
//       processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
//       remarks: 'Approved',
//       createdBy: auth.userId, 
//       status: roleName === 'Store Purchase Officer' ? 'APPROVED' : 'AWAITING APPROVAL',
//     };

//     await axios.post('/api/process-controller/approveGi', payload);

//     message.success('GI Approved successfully');
//     fetchData();
//   } catch (error) {
//     message.error(error?.response?.data?.responseStatus?.message || 'Failed to approve GI');
//   }
// };

// /*
//   const handleReject = async (record) => {
//     Modal.confirm({
//       title: 'Reject GI',
//       content: 'Are you sure you want to reject this GI?',
//       onOk: async () => {
//         try {
//           await axios.post('/api/process-controller/rejectGi', {
//             processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
//           });
//           message.success('GI Rejected successfully');
//           fetchData();
//         } catch (error) {
//           message.error(error?.response?.data?.responseStatus?.message || 'Failed to reject GI');
//         }
//       },
//     });
//   };*/
//   const handleReject = async (record) => {
//   const uniqueKey = rowKey(record);
//   try {
//     await axios.post('/api/process-controller/rejectGi', {
//       processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
//       createdBy: auth.userId,
//       remarks: rejectComments[uniqueKey],
//       status: "REJECTED",
//     });
//     message.success('GI Rejected successfully');
//     setActiveRejectKey(null);
//     setRejectComments((prev) => ({ ...prev, [uniqueKey]: '' }));
//     fetchData();
//   } catch (error) {
//     message.error(error?.response?.data?.responseStatus?.message || 'Failed to reject GI');
//   }
// };


//   const handleSubmitChangeRequest = async (record) => {
//     const uniqueKey = rowKey(record);
//     try {
//       await axios.post('/api/process-controller/changeReqGi', {
//         processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
//         createdBy: auth.userId,
//         remarks: comments[uniqueKey],
//         status: "CHANGE REQUEST",
//       });
//       message.success('GI Change Request sent successfully');
//       setActivePopoverKey(null);
//       setComments((prev) => ({ ...prev, [uniqueKey]: '' }));
//       fetchData();
//     } catch (error) {
//       message.error(error?.response?.data?.responseStatus?.message || 'Failed to send change request');
//     }
//   };
//    const handleEdit = async (record) => {
//       navigate("/inventory/goodsInspection", {state: {processNo: "INV" + record.gprnProcessId + "/" + record.inspectionSubProcessId, data: record}});
//     };

//   const columns = [ 
//     {
//       title: 'Process ID',
//      // render: (_, record) => `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
   
//   render: (_, record) => (
//     <Button type="link" onClick={() => handleProcessIdClick(record)}>
//       {`INV${record.gprnProcessId}/${record.inspectionSubProcessId}`}
//     </Button>
//   ),


//     },
//     { title: 'Location', dataIndex: 'locationId' },
//     { title: 'Installation Date', dataIndex: 'installationDate' },
//     { title: 'Commissioning Date', dataIndex: 'commissioningDate' },
//     { title: 'Status', dataIndex: 'status' },
//     {
//   title: 'Actions',
//   render: (_, record) => {
//     const uniqueKey = rowKey(record);
//     if (roleName === 'Indent Creator') {
//       return (
//         <Button type="primary" onClick={() => handleEdit(record)}>
//           Edit data
//         </Button>
//       );
//     }

//     return (
//       <Space>
//         <Button type="primary" onClick={() => handleApprove(record)}>Approve</Button>
//        {/* <Button danger onClick={() => handleReject(record)}>Reject</Button>*/}
//        <Popover
//       content={
//         <div className='ppv' style={{ width: 250 }}>
//           <TextArea
//             rows={3}
//             placeholder="Enter rejection comment"
//             value={rejectComments[uniqueKey] || ''}
//             onChange={(e) =>
//               setRejectComments((prev) => ({
//                 ...prev,
//                 [uniqueKey]: e.target.value,
//               }))
//             }
//           />
//           <Button
//             danger
//             style={{ marginTop: 8 }}
//             onClick={() => handleReject(record)}
//             disabled={!rejectComments[uniqueKey]?.trim()}
//           >
//             Reject
//           </Button>
//         </div>
//       }
//       title="Reject GI"
//       trigger="click"
//       open={activeRejectKey === uniqueKey}
//       onOpenChange={(visible) => {
//         setActiveRejectKey(visible ? uniqueKey : null);
//       }}
//     >
//       <Button danger>Reject</Button>
//     </Popover>


//         <Popover
//           content={
//             <div style={{ width: 250 }}>
//               <TextArea
//                 rows={3}
//                 placeholder="Enter comment"
//                 value={comments[uniqueKey] || ''}
//                 onChange={(e) =>
//                   setComments((prev) => ({
//                     ...prev,
//                     [uniqueKey]: e.target.value,
//                   }))
//                 }
//               />
//               <Button
//                 type="primary"
//                 style={{ marginTop: 8 }}
//                 onClick={() => handleSubmitChangeRequest(record)}
//                 disabled={!comments[uniqueKey]?.trim()}
//               >
//                 Submit
//               </Button>
//             </div>
//           }
//           title="Change Request"
//           trigger="click"
//           open={activePopoverKey === uniqueKey}
//           onOpenChange={(visible) => {
//             setActivePopoverKey(visible ? uniqueKey : null);
//           }}
//         >
//           <Button type="link">Change Request</Button>
//         </Popover>
//       </Space>
//     );
//   },

//     },
//   ];

//   return (
//     <>
//  <FilterComponent
//   searchTerm={searchTerm}
//   setSearchTerm={setSearchTerm}
//   onSearch={handleSearch}
//   onReset={handleReset}
// />




//     <Table
//      // dataSource={data}
//       dataSource={filteredData}
//       columns={columns}
//       rowKey={rowKey}
//     />
//    <InventoryQueueModal
//     modalVisible={modalVisible}
//     setModalVisible={setModalVisible}
//     selectedRecord={selectedRecord}
//     detailsData={selectedRecord} 
//     type="GI"
//     loading={loading}
//     historyVisible={false}
//     setHistoryVisible={() => {}}
//    />

// </>
//   );
// };

// export default GiApprovalPage;




import React, { useEffect, useState } from 'react';
import { Table, Button, message, Space, Modal, Input } from 'antd';
import axios from 'axios';
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom"; 
import InventoryQueueModal from './InventoryQueueModel';
import { SearchOutlined } from "@ant-design/icons";

const { TextArea } = Input;

// Search Input Component
const FilterComponent = ({ searchTerm, setSearchTerm, onSearch, onReset }) => (
  <div style={{ marginBottom: 16 }}>
    <Space>
      <Input
        placeholder="Search by Request ID"
        prefix={<SearchOutlined />}
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}  // Only update input value
        style={{ width: 300 }}
        allowClear
      />
      <Button
        type="primary"
        icon={<SearchOutlined />}
        onClick={onSearch} // Search only on button click
      >
        Search
      </Button>
      <Button onClick={onReset}>Reset</Button>
    </Space>
  </div>
);

const GiApprovalPage = () => {
  const [data, setData] = useState([]);
  const [comments, setComments] = useState({});        // change request comments per row
  const [rejectComments, setRejectComments] = useState({}); // reject comments per row

  // Modals for process details, reject, and change request
  const [modalVisible, setModalVisible] = useState(false);      // inventory queue (details) modal
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [loading, setLoading] = useState(false);

  const [rejectModal, setRejectModal] = useState({ open: false, key: null, record: null });
  const [changeModal, setChangeModal] = useState({ open: false, key: null, record: null });

  const [searchTerm, setSearchTerm] = useState("");
  const [filteredData, setFilteredData] = useState([]);

  const auth = useSelector((state) => state.auth);
  const roleName = auth.role;
  const navigate = useNavigate();

  const rowKey = (record) => `${record.gprnProcessId}_${record.inspectionSubProcessId}`;

  const fetchData = async () => {
    try {
      const url =
        roleName === 'Store Purchase Officer'
          ? '/api/process-controller/getGiByStatuses'
          : '/api/process-controller/getGiByIndentorStatuses';

      const res = await axios.get(url);
      setData(res.data.responseData || []);
      setFilteredData(res.data.responseData || []);
    } catch (error) {
      message.error('Failed to fetch GI list');
    }
  };

  const handleSearch = () => {
    if (!searchTerm.trim()) {
      setFilteredData(data);
      return;
    }
    const lower = searchTerm.toLowerCase();
    const filtered = data.filter(
      (item) =>
        `INV${item.gprnProcessId}/${item.inspectionSubProcessId}`.toLowerCase().includes(lower) ||
        (item.locationId && item.locationId.toLowerCase().includes(lower))
    );
    setFilteredData(filtered);
  };

  const handleReset = () => {
    setSearchTerm("");
    setFilteredData(data);
  };

  const handleProcessIdClick = async (record) => {
    setModalVisible(true);
    setLoading(true);
    try {
      const processNo = `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`;
      const response = await axios.get(`/api/process-controller/getSubProcessDtls?processStage=GI&processNo=${processNo}`);
      setSelectedRecord(response.data.responseData);
    } catch (error) {
      message.error("Failed to fetch process details");
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (record) => {
    try {
      const payload = {
        processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
        remarks: 'Approved',
        createdBy: auth.userId,
        status: roleName === 'Store Purchase Officer' ? 'APPROVED' : 'AWAITING APPROVAL',
      };

      await axios.post('/api/process-controller/approveGi', payload);

      message.success('GI Approved successfully');
      fetchData();
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to approve GI');
    }
  };

  const handleReject = async (record, key) => {
    try {
      await axios.post('/api/process-controller/rejectGi', {
        processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
        createdBy: auth.userId,
        remarks: rejectComments[key],
        status: "REJECTED",
      });
      message.success('GI Rejected successfully');
      setRejectModal({ open: false, key: null, record: null });
      setRejectComments((prev) => ({ ...prev, [key]: '' }));
      fetchData();
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to reject GI');
    }
  };

  const handleSubmitChangeRequest = async (record, key) => {
    try {
      await axios.post('/api/process-controller/changeReqGi', {
        processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`,
        createdBy: auth.userId,
        remarks: comments[key],
        status: "CHANGE REQUEST",
      });
      message.success('GI Change Request sent successfully');
      setChangeModal({ open: false, key: null, record: null });
      setComments((prev) => ({ ...prev, [key]: '' }));
      fetchData();
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || 'Failed to send change request');
    }
  };

  const handleEdit = (record) => {
    navigate("/inventory/goodsInspection", {
      state: { processNo: `INV${record.gprnProcessId}/${record.inspectionSubProcessId}`, data: record },
    });
  };

  // Modal open/close helpers
  const openReject = (record) => {
    const key = rowKey(record);
    setRejectModal({ open: true, key, record });
  };
  const openChange = (record) => {
    const key = rowKey(record);
    setChangeModal({ open: true, key, record });
  };
  const closeReject = () => setRejectModal({ open: false, key: null, record: null });
  const closeChange = () => setChangeModal({ open: false, key: null, record: null });

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const columns = [
    {
      title: 'Process ID',
      render: (_, record) => (
        <Button type="link" onClick={() => handleProcessIdClick(record)}>
          {`INV${record.gprnProcessId}/${record.inspectionSubProcessId}`}
        </Button>
      ),
    },
    { title: 'Location', dataIndex: 'locationId' },
    { title: 'Installation Date', dataIndex: 'installationDate' },
    { title: 'Commissioning Date', dataIndex: 'commissioningDate' },
    { title: 'Status', dataIndex: 'status' },
    {
      title: 'Actions',
      render: (_, record) => {
        const key = rowKey(record);

        if (roleName === 'Indent Creator') {
          return (
            <Button type="primary" onClick={() => handleEdit(record)}>
              Edit data
            </Button>
          );
        }

        return (
          <Space>
            <Button type="primary" onClick={() => handleApprove(record)}>Approve</Button>

            <Button danger onClick={() => openReject(record)}>Reject</Button>

            {/* <Button type="link" onClick={() => openChange(record)}>Change Request</Button> */}
          </Space>
        );
      },
    },
  ];

  return (
    <>
      <FilterComponent
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        onSearch={handleSearch}
        onReset={handleReset}
      />

      <Table
        dataSource={filteredData}
        columns={columns}
        rowKey={rowKey}
      />

      {/* Inventory Queue (details) Modal */}
      <InventoryQueueModal
        modalVisible={modalVisible}
        setModalVisible={setModalVisible}
        selectedRecord={selectedRecord}
        detailsData={selectedRecord}
        type="GI"
        loading={loading}
        historyVisible={false}
        setHistoryVisible={() => {}}
      />

      {/* Reject Modal */}
      <Modal
        title="Reject GI"
        open={rejectModal.open}
        onCancel={closeReject}
        okText="Reject"
        okButtonProps={{
          danger: true,
          disabled: !rejectComments[rejectModal.key]?.trim(),
        }}
        destroyOnClose
        onOk={() => {
          if (!rejectComments[rejectModal.key]?.trim()) return;
          handleReject(rejectModal.record, rejectModal.key);
        }}
      >
        <div className="ppv">
          <TextArea
            rows={3}
            placeholder="Enter rejection comment"
            value={rejectComments[rejectModal.key] || ''}
            onChange={(e) =>
              setRejectComments((prev) => ({
                ...prev,
                [rejectModal.key]: e.target.value,
              }))
            }
          />
        </div>
      </Modal>

      {/* Change Request Modal */}
      <Modal
        title="Change Request"
        open={changeModal.open}
        onCancel={closeChange}
        okText="Submit"
        okButtonProps={{
          type: 'primary',
          disabled: !comments[changeModal.key]?.trim(),
        }}
        destroyOnClose
        onOk={() => {
          if (!comments[changeModal.key]?.trim()) return;
          handleSubmitChangeRequest(changeModal.record, changeModal.key);
        }}
      >
        <TextArea
          rows={3}
          placeholder="Enter comment"
          value={comments[changeModal.key] || ''}
          onChange={(e) =>
            setComments((prev) => ({
              ...prev,
              [changeModal.key]: e.target.value,
            }))
          }
        />
      </Modal>
    </>
  );
};

export default GiApprovalPage;
