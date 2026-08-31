import React, {useEffect,useState} from 'react';
import CustomReport from '../../components/DKG_Report';
import { Table } from 'antd';
import { useSelector } from 'react-redux';

const SoStatus =({ onChartData, selectedBarKey, selectedPieKey }) => {
  const [reportData, setReportData] = useState([]);
  const auth = useSelector((state) => state.auth);
  const userId = auth.userId;
  const roleName = auth.role; 
  const columns = [
   {
    title: 'SO ID',
    dataIndex: 'soId',
    key: 'soId_SOS',
    filterable: true,
  },
  {
    title: 'Tender ID',
    dataIndex: 'tenderId',
    key: 'tenderId_SOS',
    filterable: true,
  },
  {
    title: 'Indent IDs',
    dataIndex: 'indentIds',
    key: 'indentIds_SOS',
    filterable: true,
  },
  {
    title: 'Vendor Name',
    dataIndex: 'vendorName',
    key: 'vendorName_SOS',
    filterable: true,
  },
  {
    title: 'Value',
    dataIndex: 'value',
    key: 'value_SOS',
    filterable: true,
  },
  
  {
    title: 'Submitted Date',
    dataIndex: 'submittedDate',
    key: 'submittedDate_SOS',
    filterable: true,
  },
  {
    title: 'Pending With',
    dataIndex: 'pendingWith',
    key: 'pendingWith_SOS',
    filterable: true,
  },
  
  {
    title: 'Pending From',
    dataIndex: 'pendingFrom',
    key: 'pendingFrom_SOS',
    filterable: true,
  },
    {
    title: 'status',
    dataIndex: 'status',
    key: 'status_SOS',
    filterable: true,
  },
  {
    title: 'On Date',
    dataIndex: 'asOnDate',
    key: 'asOnDate_SOS',
    filterable: true,
  },
    {
      title: 'Service Order Materials',
      dataIndex: 'materials',
      key: 'materials_SOS',
      render: (materials) => (
        <Table
          dataSource={materials}
          pagination={false}
          columns={[
            {
    title: 'Job Code',
    dataIndex: 'jobCode',
    key: 'jobCode',
  },
  {
    title: 'Job Description',
    dataIndex: 'jobDescription',
    key: 'jobDescription',
  },
  {
    title: 'Quantity',
    dataIndex: 'quantity',
    key: 'quantity',
  },
  {
    title: 'Rate',
    dataIndex: 'rate',
    key: 'rate',
  },
  {
    title: 'Currency',
    dataIndex: 'currency',
    key: 'currency',
  },
  {
    title: 'Exchange Rate',
    dataIndex: 'exchangeRate',
    key: 'exchangeRate',
  },
  {
    title: 'GST',
    dataIndex: 'gst',
    key: 'gst',
  },
  {
    title: 'Duties',
    dataIndex: 'duties',
    key: 'duties',
  },
  {
    title: 'budgetCode',
    dataIndex: 'budgetCode',
    key: 'budgetCode',
  },
          ]}
        />
      )
    }
  ];

  const api = "/api/reports/pending-so-report";

   const handleFetch = (startDate, endDate, data) => {
    const finalData = data || [];
    setReportData(finalData);
    generateChart(finalData);
  };

  const generateChart = (finalData) => {
    const barDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedBarKey] || "Unknown";
      acc[key] = (acc[key] || 0) + (item.value || 0);
      return acc;
    }, {});

    const pieDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedPieKey] || "No Data";
      acc[key] = (acc[key] || 0) + (item.value || 0);
      return acc;
    }, {});

    const barData = Object.keys(barDataMap).map(k => ({ name: k, value: barDataMap[k] }));
    const pieData = Object.keys(pieDataMap).map(k => ({ name: k, value: pieDataMap[k] }));

    if (onChartData) onChartData(barData, pieData);
  };

  useEffect(() => {
    if (reportData.length > 0) {
      generateChart(reportData);
    }
  }, [selectedBarKey, selectedPieKey]);

  return (
    <div>
      <CustomReport columns={columns} api={api} title="So Status" filterType="date" storageKey="SOSTATUS_REPORT_COLUMNS" onFetch={handleFetch} userId={userId} roleName={roleName}/>
    </div>
  );
};

export default SoStatus;
