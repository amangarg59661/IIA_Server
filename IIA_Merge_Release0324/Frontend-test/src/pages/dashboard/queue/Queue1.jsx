// import { Tabs } from 'antd'
// import React , {useState, useEffect} from 'react'
// import QueueRequest from './QueueRequest'
// import axios from "axios";

// const Queue1 = () => {

//   const [data, setData] = useState([]);
// const [counts, setCounts] = useState({});
// useEffect(() => {
//   fetchData();
// }, []);

// const fetchData = async () => {
//   try {
//     const response = await axios.get(`/pendingWorkflowTransitionQueue`);

//     const responseData = response.data.responseData;

//     const formattedData = responseData.map((item) => ({
//       ...item,
//     }));

//     setData(formattedData);

//     // ✅ calculate counts
//     const workflowCounts = {};
//     formattedData.forEach((item) => {
//       const id = item.workflowId;
//       workflowCounts[id] = (workflowCounts[id] || 0) + 1;
//     });

//     setCounts(workflowCounts);

//   } catch (error) {
//     console.error(error);
//   }
// };



// return (
//     <Tabs
//       items={[
//         {
//           key: 'IND',
//           label: `Indent (${counts[1] || 0})`,
//           children: <QueueRequest workflowId={1} data={data} />,
//         },
//         {
//           key: 'T',
//           label: `Tender (${counts[1] || 0})`,
//           children: <QueueRequest requestType="Tender" data={data} />,
//         },
//         {
//           key: 'CP',
//           label: `Contingency Purchase (${counts[1] || 0})`,
//           children: <QueueRequest workflowId={2} data={data} />,
//         },
//         {
//           key: 'PO',
//           label: `Purchase Order (${counts[1] || 0})`,
//           children: <QueueRequest workflowId={3} data={data} />,
//         },
//         {
//           key: 'SO',
//           label: `Service Order(${counts[1] || 0})`,
//           children: <QueueRequest workflowId={5} data={data} />,
//         },
//         {
//           key: 'M',
//           label: `Material (${counts[1] || 0})`,
//           children: <QueueRequest requestType="M" data={data} />,
//         },
//         {
//           key: 'J',
//           label: `Job (${counts[1] || 0})`,
//           children: <QueueRequest requestType="J" data={data} />,
//         },
//         {
//           key: 'V',
//           label: `Vendor (${counts[1] || 0})`,
//           children: <QueueRequest requestType="V" data={data} />,
//         },
//         {
//           key: 'C',
//           label: `Cancelled Indents (${counts[1] || 0})`,
//           children: <QueueRequest requestType="C" data={data} />,
//         },
//         {
//           key: 'PV',
//           label: `Payment Voucher(${counts[1] || 0})`,
//           children: <QueueRequest requestType="PV" data={data} />,
//         },
//       ]}
//     />
//   )
// }

// export default Queue1



import { Tabs, Spin } from 'antd';
import React, { useState, useEffect, useCallback } from 'react';
import QueueRequest from './QueueRequest';
import axios from 'axios';
import { useSelector } from 'react-redux';

const Queue1 = () => {
  const auth = useSelector((state) => state.auth);
  const { userId } = useSelector((state) => state.auth);

  const [allData, setAllData] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchData = useCallback(async () => {
    if (!auth?.role) return;
    setLoading(true);
    try {
      const roleName = auth.role;
      const isPurchaseHead = roleName === 'Purchase Head';

      let responseData = [];

      // Cancelled indents for Purchase Head come from a different endpoint
      // For all other cases (including Purchase Head non-cancelled tabs),
      // we fetch from the standard queue endpoint and merge if needed.
      const params = new URLSearchParams();
      params.append('roleName', roleName);
      if (userId) params.append('userId', userId);

      if (isPurchaseHead) {
        // Fetch both: completed indents + cancelled indents in parallel
        const [mainRes, cancelRes] = await Promise.all([
          axios.get(`/completedIndentWorkflowTransition?${params.toString()}`),
          axios.get('/allCancledIndents'),
        ]);
        // Merge; cancelled ones will be filtered into the 'C' tab later
        responseData = [
          ...(mainRes.data.responseData || []),
          ...(cancelRes.data.responseData || []),
        ];
      } else {
        const response = await axios.get(`/pendingWorkflowTransitionQueue?${params.toString()}`);
        responseData = response.data.responseData || [];
      }

      // Format exactly as QueueRequest used to
      const formattedData = responseData
        .map((item) => ({
          key: item.requestId,
          requestId: item.requestId,
          workflowId: item.workflowId,
          workflowName: item.workflowName,
          createdDate: new Date(item.createdDate),
          remarks: item.transitionHistory?.[0]?.remarks || 'No remarks',
          status: item.nextAction,
          action: item.action,
          amount: item.amount,
          paymentType: item.paymentType,
          poNo: item.poNO,
          vendorName: item.vendorName,
          workflowTransitionId: item.workflowTransitionId,
          assignedToUserId: item.assignedToUserId,
          assignedToEmployeeName: item.assignedToEmployeeName,

          ...(item.workflowId === 1 && {
            indentorName: item.indentorName,
            amount: item.amount,
            projectName: item.projectName,
            budgetName: item.budgetName,
            modeOfProcurement: item.modeOfProcurement,
            consignee: item.consignee,
            status: item.status,
            action: item.action,
          }),
          ...(item.workflowId === 2 && {
            createdBy: item.createdBy,
            amount: item.amount,
            projectName: item.projectName,
            consignee: item.deliveryLocation,
          }),
          ...(item.workflowId === 3 && {
            createdBy: item.createdBy,
            amount: item.amount,
            projectName: item.projectName,
            budgetCode: item.budgetCode,
            procurementType: item.procurementType,
            modeOfProcurement: item.modeOfProcurement,
            consignee: item.consignee,
          }),
          ...((item.workflowId === 4 || item.workflowId === 7) && {
            createdBy: item.createdBy,
            projectName: item.projectName,
            budgetCode: item.budgetCode,
            modeOfProcurement: item.modeOfProcurement,
            consignee: item.consignee,
            amount: item.amount,
          }),
          ...(item.workflowId === 5 && {
            createdBy: item.createdBy,
            projectName: item.projectName,
            budgetCode: item.budgetCode,
            procurementType: item.procurementType,
            consignee: item.consignee,
          }),
          ...(item.workflowId === 9 && {
            indentorName: item.indentorName,
            amount: item.amount,
          }),
          ...(item.workflowId === 10 && {
            indentorName: item.indentorName,
            amount: item.amount,
            poNo: item.poNo,
            vendorName: item.vendorName,
            paymentType: item.paymentType,
          }),
        }))
        .sort((a, b) => b.createdDate - a.createdDate);

      setAllData(formattedData);
    } catch (error) {
      console.error('Queue1 fetchData error:', error);
    } finally {
      setLoading(false);
    }
  }, [auth?.role, userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // ---------------------------------------------------------------------------
  // Filtering helpers — same logic that was inside QueueRequest.fetchData
  // ---------------------------------------------------------------------------
  const byWorkflowId = (id) => allData.filter((item) => item.workflowId === id);
  const byWorkflowName = (name) => allData.filter((item) => item.workflowName === name);
  const tenderData = allData.filter((item) => item.workflowId === 4 || item.workflowId === 7);
  const cancelledData = allData.filter((item) => item.action === 'Indentor Cancelled');
  const pvData = allData.filter((item) => item.workflowId === 10);

  const tabs = [
    {
      key: 'IND',
      label: `Indent (${byWorkflowId(1).length})`,
      data: byWorkflowId(1),
      props: { workflowId: 1 },
    },
    {
      key: 'T',
      label: `Tender (${tenderData.length})`,
      data: tenderData,
      props: { requestType: 'Tender' },
    },
    {
      key: 'CP',
      label: `Contingency Purchase (${byWorkflowId(2).length})`,
      data: byWorkflowId(2),
      props: { workflowId: 2 },
    },
    {
      key: 'PO',
      label: `Purchase Order (${byWorkflowId(3).length})`,
      data: byWorkflowId(3),
      props: { workflowId: 3 },
    },
    {
      key: 'SO',
      label: `Service Order (${byWorkflowId(5).length})`,
      data: byWorkflowId(5),
      props: { workflowId: 5 },
    },
    {
      key: 'M',
      label: `Material (${byWorkflowName('Material Workflow').length})`,
      data: byWorkflowName('Material Workflow'),
      props: { requestType: 'M' },
    },
    {
      key: 'J',
      label: `Job (${byWorkflowName('Job Workflow').length})`,
      data: byWorkflowName('Job Workflow'),
      props: { requestType: 'J' },
    },
    {
      key: 'V',
      label: `Vendor (${byWorkflowName('Vendor Workflow').length})`,
      data: byWorkflowName('Vendor Workflow'),
      props: { requestType: 'V' },
    },
    {
      key: 'C',
      label: `Cancelled Indents (${cancelledData.length})`,
      data: cancelledData,
      props: { requestType: 'C' },
    },
    {
      key: 'PV',
      label: `Payment Voucher (${pvData.length})`,
      data: pvData,
      props: { requestType: 'PV' },
    },
  ];

  if (loading) {
    return <Spin size="large" tip="Loading queue..." style={{ marginTop: 48, display: 'block' }} />;
  }

  return (
    <Tabs
      items={tabs.map(({ key, label, data, props }) => ({
        key,
        label,
        children: (
          <QueueRequest
            {...props}
            data={data}
            loading={loading}
            refetchData={fetchData}
          />
        ),
      }))}
    />
  );
};

export default Queue1;
