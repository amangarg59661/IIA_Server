import {React, useEffect, useState} from 'react'
import CustomReport from '../../components/DKG_Report';
import { useSelector } from 'react-redux';

const AssetReport =  ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const auth = useSelector((state) => state.auth);
  const userId = auth.userId;
  const roleName = auth.role;
  const [reportData, setReportData] = useState([]);
    const columns = [
        { title: 'Asset ID', dataIndex: 'assetId', key: 'assetId_AssetReport', searchable: true },
        { title: 'Asset Code', dataIndex: 'assetCode', key: 'assetCode_AssetReport', searchable: true },
        { title: 'Asset Description', dataIndex: 'assetDesc', key: 'assetDesc_AssetReport', searchable: true },
        { title: 'Asset Category', dataIndex: 'category', key: 'category_AssetReport', searchable: true },
        { title: 'Asset Sub Category', dataIndex: 'subCategory', key: 'subCategory_AssetReport', searchable: true },
        { title: 'Assigned', dataIndex: 'assignedTo', key: 'assignedTo_AssetReport', searchable: true },
        { title: 'Locator', dataIndex: 'location', key: 'location_AssetReport', searchable: true },
        { title: 'PO ID', dataIndex: 'poId', key: 'poId_AssetReport', searchable: true },
        { title: 'PO Value', dataIndex: 'poValue', key: 'poValue_AssetReport', searchable: true },
        { title: 'GRIN Date', dataIndex: 'purchaseDate', key: 'purchaseDate_AssetReport', searchable: true },
        { title: 'Payment Voucher Number', dataIndex: 'invoiceNo', key: 'invoiceNo_AssetReport', searchable: true },
        { title: 'Payment Voucher Date', dataIndex: 'invoiceDate', key: 'invoiceDate_AssetReport', searchable: true },
        { title: 'Vendor Id', dataIndex: 'vendorId', key: 'vendorId_AssetReport', searchable: true },
        { title: 'Vendor Name', dataIndex: 'vendorName', key: 'vendorName_AssetReport', searchable: true },
        { title: 'Condition', dataIndex: 'conditionOfGoods', key: 'conditionOfGoods_AssetReport', searchable: true },
        { title: 'Remarks', dataIndex: 'remarks', key: 'remarks_AssetReport', searchable: true },

    ];
    localStorage.getItem('ASSET_REPORT_COLUMNS')
    
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
      <CustomReport columns={columns} api={api} title="Asset Report"   filterType="none"  storageKey="ASSET_REPORT_COLUMNS" onFetch={handleFetch}
        userId={userId}
        roleName={roleName}/>
    </div>
  )
}

export default AssetReport
