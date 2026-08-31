import React, { useEffect, useState } from 'react';
import CustomReport from '../../../components/DKG_Report';
import { Button, message, Table, Space, Popover, Input } from 'antd';
import axios from 'axios';
import TableComponent from '../../../components/DKG_Table';
import { useNavigate } from 'react-router-dom';
import Btn from '../../../components/DKG_Btn';
import { useSelector } from 'react-redux';

const PendingGi = () => {
    const navigate = useNavigate();

    const handleApprove = async (record) => {
      try {
        await axios.post(`/api/process-controller/approveGprn`, {processNo: "INV" + record.processId + "/" + record.subProcessId,}, {params: {actionBy: userId}});
        message.success('GPRN approved successfully');
        populateData();
      } catch (error) {
        message.error(error?.response?.data?.responseStatus?.message || 'Failed to approve GPRN');
      }
    };
    const handleEdit = async (record) => {
      navigate("/inventory/gprn", {state: {processNo: "INV" + record.processId + "/" + record.subProcessId, data: record}});
    };

    const handleReject = async (record) => {
      try {
        await axios.post(`/api/process-controller/rejectGprn`, {
          processNo: "INV" + record.processId + "/" + record.subProcessId,
        }, {params: {actionBy: userId}});
        message.success('GPRN rejected successfully');
        // setRejectComment('');
        populateData();
      } catch (error) {
        message.error(error?.response?.data?.responseStatus?.message || 'Failed to reject GPRN');
      }
    };

    const changeRequest = async (record) => {
      try {
        await axios.post(`/api/process-controller/changeReqGprn`, {
          processNo: "INV" + record.processId + "/" + record.subProcessId,
        }, {params: {actionBy: userId}});
        message.success('GPRN change request successful.');
        // setRejectComment('');
        populateData();
      } catch (error) {
        message.error(error?.response?.data?.responseStatus?.message || 'Failed to reject GPRN');
      }
    };

    const columns = [
    { title: 'Process ID', dataIndex: 'processId', key: 'processId', searchable: true, render: (_, record) => "INV" + record.processId + "/" + record.subProcessId, fixed: 'left'   },
    // { title: 'Sub Process ID', dataIndex: 'subProcessId', key: 'subProcessId', searchable: true },
    { title: 'PO ID', dataIndex: 'poId', key: 'poId', searchable: true, fixed: 'left'  },
    { title: 'Location', dataIndex: 'locationId', key: 'locationId', filterable: true },
    { title: 'Date', dataIndex: 'date', key: 'date' },
    { title: 'Challan No', dataIndex: 'challanNo', key: 'challanNo', searchable: true },
    { title: 'Delivery Date', dataIndex: 'deliveryDate', key: 'deliveryDate' },
    { title: 'Vendor ID', dataIndex: 'vendorId', key: 'vendorId', searchable: true },
    { title: 'Field Station', dataIndex: 'fieldStation', key: 'fieldStation', filterable: true },
    { title: 'Indentor Name', dataIndex: 'indentorName', key: 'indentorName', searchable: true },
    { title: 'Supply Expected Date', dataIndex: 'supplyExpectedDate', key: 'supplyExpectedDate' },
    { title: 'Consignee Detail', dataIndex: 'consigneeDetail', key: 'consigneeDetail', searchable: true },
    { title: 'Warranty Years', dataIndex: 'warrantyYears', key: 'warrantyYears' },
    { title: 'Project', dataIndex: 'project', key: 'project', filterable: true },
    { title: 'Received By', dataIndex: 'receivedBy', key: 'receivedBy', searchable: true },
    {
      title: 'Material Details',
      dataIndex: 'materialDetails',
      key: 'materialDetails',
      render: (materialDetails) => (
        <Table
          dataSource={materialDetails}
          pagination={false}
          columns={[
            { title: 'Detail ID', dataIndex: 'detailId', key: 'detailId' },
            { title: 'Material Code', dataIndex: 'materialCode', key: 'materialCode' },
            { title: 'Material Description', dataIndex: 'materialDesc', key: 'materialDesc' },
            { title: 'UOM', dataIndex: 'uomId', key: 'uomId' },
            { title: 'Received Quantity', dataIndex: 'receivedQuantity', key: 'receivedQuantity' },
            { title: 'Unit Price', dataIndex: 'unitPrice', key: 'unitPrice' },
            { title: 'Make No', dataIndex: 'makeNo', key: 'makeNo' },
            { title: 'Serial No', dataIndex: 'serialNo', key: 'serialNo' },
            { title: 'Model No', dataIndex: 'modelNo', key: 'modelNo' },
            { title: 'Warranty Terms', dataIndex: 'warrantyTerms', key: 'warrantyTerms' },
            { title: 'Note', dataIndex: 'note', key: 'note' },
            { title: 'Photo Path', dataIndex: 'photoPath', key: 'photoPath' },
          ]}
        />
      )
    },
    { 
      title: 'Status', 
      dataIndex: 'status', 
      key: 'status',
      filterable: true 
    },
    {
      title: "Actions", 
      dataIndex: "actions",
      render: (_, record) => {
        if (record.status === 'APPROVED') {
          return (
            <Button 
              className='hover:!border-darkBlueHover !border-darkBlue !text-darkBlue hover:!text-darkBlueHover' 
              onClick={() => navigate("/inventory/goodsInspection", {
                state: {processNo: "INV" + record.processId + "/" + record.subProcessId}
              })}
            >
              Create GI
            </Button>
          );
        }
        
        if (record.status === 'AWAITING APPROVAL') {
          return (
            <Space>
              <Button type="primary" onClick={() => handleApprove(record)}>
                Approve
              </Button>
              <Button
                danger
                onClick={() => handleReject(record)}> Reject 
              </Button>
              <Button
                onClick={() => changeRequest(record)}> Change Request 
              </Button>
            </Space>
          )
        }

        if(record.status.trim() === "CHANGE REQUEST"){
          return(
            <Button type="primary" onClick={() => handleEdit(record)}>
                Edit data
          </Button>
          )
        }
        console.log("status", record.status, record.status.length);
        return null; // For REJECTED status
      }
    }
  ];

  const { userId } = useSelector(state => state.auth)

  const api = "/api/process-controller/getGiStatusWise?status=AWAITING%20APPROVAL";
  const api2 = `/api/process-controller/getGiStatusWise?status=CHANGE%20REQUEST&createdBy=${userId}`;

  const [ds, setDs] = useState([]);

  const populateData = async () => {
    try{
        const [apiData, api2Data] = await Promise.all([axios.get(api), axios.get(api2)]);
        const responseData = apiData?.data?.responseData || [];
        const responseData2 = api2Data?.data?.responseData || [];
        setDs([...responseData, ...responseData2]);
    }
    catch(e){
        message.error("Erorr fetching details.");
        console.log("E: ", e);
    }
  }
  useEffect(() => {
    populateData();
  }, [])

  return (
    <div>
      <TableComponent dataSource={ds} columns={columns} storageKey="GIPENDING_REPORT_COLUMNS"/>
    </div>
  );
};

export default PendingGi;
