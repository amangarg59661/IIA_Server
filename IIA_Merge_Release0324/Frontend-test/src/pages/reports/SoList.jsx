import React ,{useState,useEffect } from 'react';
import CustomReport from '../../components/DKG_Report';
import { Table } from 'antd';
import { useSelector } from 'react-redux'

const SoList =  ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const [reportData, setReportData] = useState([]);
  const auth = useSelector((state) => state.auth);
  const userId = auth.userId;
  const roleName = auth.role; 
  const columns = [
    {
    title: 'Approved Date',
    dataIndex: 'approvedDate',
    key: 'approvedDate_SO',
    filterable: true,
  },
  {
    title: 'SO ID',
    dataIndex: 'soId',
    key: 'soId_SO',
    filterable: true,
  },
  {
    title: 'Vendor Name',
    dataIndex: 'vendorName',
    key: 'vendorName_SO',
    filterable: true,
  },
  {
    title: 'Value',
    dataIndex: 'value',
    key: 'value_SO',
    filterable: true,
  },
  {
    title: 'Tender ID',
    dataIndex: 'tenderId',
    key: 'tenderId_SO',
    filterable: true,
  },
  {
    title: 'Project',
    dataIndex: 'project',
    key: 'project_SO',
    filterable: true,
  },
  {
    title: 'Vendor ID',
    dataIndex: 'vendorId',
    key: 'vendorId_SO',
    filterable: true,
  },
  {
    title: 'Indent IDs',
    dataIndex: 'indentIds',
    key: 'indentIds_SO',
    filterable: true,
  },
  {
    title: 'Mode of Procurement',
    dataIndex: 'modeOfProcurement',
    key: 'modeOfProcurement_SO',
    filterable: true,
  },
    {
      title: 'Service Order Materials',
      dataIndex: 'materials',
      key: 'materials_SO',
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

  const api = "/api/reports/soList-report";

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
      <CustomReport columns={columns} api={api} title="So List" filterType="date" storageKey="SOLIST_REPORT_COLUMNS" onFetch={handleFetch} userId={userId} roleName={roleName}/>
    </div>
  );
};

export default SoList;
