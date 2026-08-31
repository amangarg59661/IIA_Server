import {React, useEffect, useState} from 'react';
import CustomReport from '../../components/DKG_Report';
import { Table } from 'antd';

const AssetDisposalReport = ({ onChartData, selectedBarKey, selectedPieKey }) => {
  const [reportData, setReportData] = useState([]);

  const columns = [
    { title: 'Disposal ID', dataIndex: 'disposalId', key: 'disposalId', render: (text) => `INV/${text}`, searchable: true },
    { title: 'Disposal Date', dataIndex: 'disposalDate', key: 'disposalDate', render: (text) => text ? new Date(text).toLocaleDateString() : '' },
    { title: 'Location', dataIndex: 'locationId', key: 'locationId', searchable: true },
    {title: 'Auction Id', dataIndex:'auctionId',key:'auctionId', searchable: true},
    { title: 'Custodian ID', dataIndex: 'custodianId', key: 'custodianId', searchable: true },
    { title: 'Custodian Name', dataIndex: 'custodianName', key: 'custodianName', searchable: true },
    { title: 'Status', dataIndex: 'status', key: 'status', searchable: true },
    {title: "Store Purchase Officer", dataIndex:"action", key: 'action', searchable:true},
    {
      title: 'Material Details',
      dataIndex: 'materialDtlList',
      key: 'materialDtlList',
      render: (details) => (
        <Table
          dataSource={details}
          pagination={false}
          columns={[
            { title: 'Asset ID', dataIndex: 'assetId', key: 'assetId' },
            { title: 'Asset Code', dataIndex: 'assetCode', key: 'assetCode' },
            { title: 'Asset Desc', dataIndex: 'assetDesc', key: 'assetDesc' },
            { title: 'UOM', dataIndex: 'uom', key: 'uom' },
            { title: 'Quantity', dataIndex: 'quantity', key: 'quantity' },
            { title: 'Disposal Category', dataIndex: 'disposalCategory', key: 'disposalCategory' },
            { title: 'Disposal Mode', dataIndex: 'disposalMode', key: 'disposalMode' },
            { title: 'Book Value', dataIndex: 'bookValue', key: 'bookValue' },
            { title: 'Unit Price', dataIndex: 'unitPrice', key: 'unitPrice' },
            { title: 'PO ID', dataIndex: 'poId', key: 'poId' },
            { title: 'PO Value', dataIndex: 'poValue', key: 'poValue' },
            { title: 'GRIN Date', dataIndex: 'grinDate', key: 'grinDate' },
            { title: 'Reason for Disposal', dataIndex: 'reasonForDisposal', key: 'reasonForDisposal' },
          ]}
        />
      ),
    },
  ];

  const api = "/api/reports/ApprovedAssetDisposals"; // backend API

  const handleFetch = (startDate, endDate, data) => {
    const finalData = data || [];
    setReportData(finalData);
    generateChart(finalData);
  };

 
  const generateChart = (finalData) => {
    const barDataMap = finalData.reduce((acc, item) => {
      const key = item[selectedBarKey] || "Unknown";
      acc[key] = (acc[key] || 0) + 1; // counting occurrences
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
        title="Asset Disposal Report"
        filterType="none" 
        storageKey="ASSET_DISPOSAL_REPORT_COLUMNS"
        onFetch={handleFetch}
      />
    </div>
  );
};

export default AssetDisposalReport;
