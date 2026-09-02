import { React, useEffect, useState } from 'react';
import CustomReport from '../../components/DKG_Report';

const CycleCountReport = ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const [reportData, setReportData] = useState([]);

  const columns = [
    { title: 'Verification Date', dataIndex: 'verificationDate', key: 'verificationDate', searchable: true },
    { title: 'Item Description', dataIndex: 'itemDescription', key: 'itemDescription', searchable: true },
    { title: 'Category', dataIndex: 'category', key: 'category', searchable: true },
    { title: 'Sub-Category', dataIndex: 'subCategory', key: 'subCategory', searchable: true },
    { title: 'Location', dataIndex: 'location', key: 'location' },
    { title: 'Quantity As Per Records', dataIndex: 'quantityAsPerRecords', key: 'quantityAsPerRecords' },
    { title: 'Quantity Found', dataIndex: 'quantityFound', key: 'quantityFound', searchable: true },
    { title: 'Discrepancy Quantity', dataIndex: 'discrepancyQty', key: 'discrepancyQty' },
    { title: 'Discrepancy Value', dataIndex: 'discrepancyValue', key: 'discrepancyValue', searchable: true },
    { title: 'Verified By', dataIndex: 'verifiedBy', key: 'verifiedBy', searchable: true },
    { title: 'Verified', dataIndex: 'verified', key: 'verified', searchable: true },
    { title: 'Remarks', dataIndex: 'remarks', key: 'remarks', searchable: true },
  ];

  // Backend API for GRN Receipt Report, supports ?fromDate=&toDate=&category=&status=
  const api = "/api/process-controller/getCycleCountReport";

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
        title="Cycle Count Report"
        filterType="date"
        storageKey="CYCLE_COUNT_REPORT_COLUMNS"
        onFetch={handleFetch}
      />
    </div>
  );
};

export default CycleCountReport;
