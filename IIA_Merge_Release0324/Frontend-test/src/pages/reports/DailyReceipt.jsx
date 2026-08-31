import { React, useEffect, useState } from 'react';
import CustomReport from '../../components/DKG_Report';

const DailyReceipt = ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const [reportData, setReportData] = useState([]);

  const columns = [
    { title: 'GRIN ID', dataIndex: 'grinId', key: 'grinId', searchable: true },
    { title: 'GRIN No', dataIndex: 'grinNo', key: 'grinNo', searchable: true },
    { title: 'Date', dataIndex: 'date', key: 'date' },
    { title: 'Item Description', dataIndex: 'itemDescription', key: 'itemDescription', searchable: true },
    { title: 'Category', dataIndex: 'category', key: 'category', searchable: true },
    { title: 'Sub-Category', dataIndex: 'subCategory', key: 'subCategory', searchable: true },
    { title: 'Quantity Received', dataIndex: 'quantityReceived', key: 'quantityReceived' },
    { title: 'UOM', dataIndex: 'uom', key: 'uom' },
    { title: 'Supplier / Vendor Name', dataIndex: 'vendorName', key: 'vendorName', searchable: true },
    { title: 'Invoice No & Date', dataIndex: 'invoiceNoAndDate', key: 'invoiceNoAndDate' },
    { title: 'Received By', dataIndex: 'receivedBy', key: 'receivedBy', searchable: true },
    { title: 'Location', dataIndex: 'location', key: 'location', searchable: true },
    { title: 'Indentor', dataIndex: 'indentor', key: 'indentor', searchable: true },
    { title: 'PO Number', dataIndex: 'poNumber', key: 'poNumber', searchable: true },
  ];

  // Backend API for GRN Receipt Report, supports ?fromDate=&toDate=&category=&status=
  const api = "/api/grn/report";

  const handleFetch = (startDate, endDate, data) => {
    const finalData = data || [];
    setReportData(finalData);
    generateChart(finalData);
  };

  const generateChart = (finalData) => {
    const barDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedBarKey] || "Unknown";
      acc[key] = (acc[key] || 0) + 1;
      return acc;
    }, {});

    const pieDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedPieKey] || "No Data";
      acc[key] = (acc[key] || 0) + 1;
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
      <CustomReport
        columns={columns}
        api={api}
        title="GRN Receipt Report"
        filterType="date"
        storageKey="GRN_REPORT_COLUMNS"
        onFetch={handleFetch}
      />
    </div>
  );
};

export default DailyReceipt;
