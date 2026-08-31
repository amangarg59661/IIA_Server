import {React, useEffect, useState} from 'react'
import CustomReport from '../../components/DKG_Report';
import { useSelector } from 'react-redux';

const PersonalInventory =  ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const auth = useSelector((state) => state.auth);
  const userId = auth.userId;
  const roleName = auth.role;
  const [reportData, setReportData] = useState([]);
    const columns = [
        {title: 'Custodian Name', dataIndex:'custodianName' , key:'custodianName_PIReport', searchable: true},
        { title: 'Asset ID', dataIndex: 'assetId', key: 'assetId_PIReport', searchable: true },
        {title: 'Asset Code' , dataIndex: 'assetCode', key: 'assetCode_PIReport', searchable: true},
        { title: 'Asset Description', dataIndex: 'assetDesc', key: 'assetDesc_PIReport', searchable: true },
        { title: 'Material Code', dataIndex: 'materialCode', key: 'materialCode_PIReport', searchable: true },
        { title: 'Material Description', dataIndex: 'materialDesc', key: 'materialDesc_PIReport', searchable: true },
        {title: 'Material Category', dataIndex: 'materialCategory', key: 'materialCategory_PIReport', searchable: true},
        {title: 'Material Sub-Category', dataIndex: 'materialSubCategory', key: 'materialSubCategory_PIReport', searchable: true},
        { title: 'Quantity', dataIndex: 'quantity', key: 'quantity_PIReport', searchable: true },
        { title: 'UOM', dataIndex: 'uomId', key: 'uomId_PIReport', filterable: true },
        { title: 'PO ID', dataIndex: 'poId', key: 'poId_PIReport', searchable: true },
        { title: 'PO Value', dataIndex: 'poValue', key: 'poValue_PIReport', searchable: true },
        { title: 'GRIN Date', dataIndex: 'grinDate', key: 'grinDate_PIReport', searchable: true },
        { title: 'Locator', dataIndex: 'locator', key: 'locator_PIReport', searchable: true },
        { title: 'Current Status', dataIndex: 'currentStatus', key: 'currentStatus_PIReport', searchable: true },
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

export default PersonalInventory;
