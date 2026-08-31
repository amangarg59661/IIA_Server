import {React, useEffect, useState} from 'react'
import CustomReport from '../../components/DKG_Report';
import { useSelector } from 'react-redux';

const ClosingBalance =  ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const auth = useSelector((state) => state.auth);
  const userId = auth.userId;
  const roleName = auth.role;
  const [reportData, setReportData] = useState([]);
    const columns = [
       { title: 'Material Code', dataIndex: 'materialCode', key: 'materialCode', searchable: true },
        { title: 'Material Description', dataIndex: 'materialDesc', key: 'materialCategory', searchable: true },
        {title: 'Material Category', dataIndex: 'materialCategory', key: 'materialCategory ', searchable: true},
        {title: 'Material Sub-Category', dataIndex: 'materialSubCategory', key: 'materialSubCategory', searchable: true},
        { title: 'Opening Balance Quantity', dataIndex: 'openingBalanceQty', key: 'openingBalanceQty', searchable: true },
        { title: 'Opening Balance Amount', dataIndex: 'openingBalanceAmt', key: 'openingBalanceAmt', searchable: true },
        { title: 'Addition during Year Quantity', dataIndex: 'additionInYearQty', key: 'additionInYearQty ', filterable: true },
        { title: 'Addition during Year Amount', dataIndex: 'additionInYearAmt', key: 'additionInYearAmt ', filterable: true },
        { title: ' Disposal Balance Quantity', dataIndex: ' disposalQty', key: ' disposalQty', searchable: true },
        { title: ' Disposal Amount', dataIndex: ' disposalAmt', key: ' disposalAmt', searchable: true },
        { title: 'Closing Balance Quantity', dataIndex: 'closingBalanceQty', key: 'closingBalanceQty', searchable: true },
        { title: 'Closing Balance Amount', dataIndex: 'closingBalanceAmt', key: 'closingBalanceAmt', searchable: true },
        {title: 'Field Station', dataIndex: 'fieldStation', key: 'fieldStation', searchable: true },
        {title: 'Consumed Quantity', dataIndex: 'consumedQty', key: 'consumedQty', searchable: true },
        {title: 'Average Purchase Value', dataIndex: 'averagePurchaseVal', key: 'averagePurchaseVal', searchable: true },
        
    ];
    localStorage.getItem('PI_REPORT_COLUMNS')
    
       const api = "/api/reports/asset"
        const handleFetch = (data) => {
    const finalData = data || [];
    setReportData(finalData);
    generateChart(finalData);
  };

  // Chart generation for bar & pie
  const generateChart = (finalData) => {
    const barDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedBarKey] || "Unknown";
      acc[key] = (acc[key] || 0) + (item.poValue || 0); // use poValue for numeric chart values
      return acc;
    }, {});

    const pieDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedPieKey] || "No Data";
      acc[key] = (acc[key] || 0) + (item.poValue || 0);
      return acc;
    }, {});

    const barData = Object.keys(barDataMap).map(k => ({ name: k, value: barDataMap[k] }));
    const pieData = Object.keys(pieDataMap).map(k => ({ name: k, value: pieDataMap[k] }));

    if (onChartData) onChartData(barData, pieData);
  };

  // Regenerate charts when selected keys change
  useEffect(() => {
    if (reportData.length > 0) generateChart(reportData);
  }, [selectedBarKey, selectedPieKey]);
  return (
    <div>
      <CustomReport columns={columns} api={api} title="Personal Inventory Report"   filterType="none"  storageKey="PI_REPORT_COLUMNS" onFetch={handleFetch}
        userId={userId}
        roleName={roleName}/>
    </div>
  )
}

export default ClosingBalance;
