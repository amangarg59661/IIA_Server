import React, { useEffect, useState } from "react";
import { message, Table, Button, Space } from "antd";
import axios from "axios";
import TableComponent from "../../../components/DKG_Table";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

const GatePass = () => {
  const { role } = useSelector((state) => state?.auth);
  const userId =useSelector((state) =>state?.auth?.userId)

  const handleApprove = async (record) => {
    if(record.formType === "GT"){
      /*
      try{
        await axios.post("/api/process-controller/approveGtOgp", {ogpId: record.ogpId})
        message.success("Gate Pass Approved Successfully");
        fetchGatePassData();
      }
      catch(error){
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to approve Gate Pass"
        );
      }
      return*/
       if (role === "Indent Creator") {
        // Receiver Approval
        await axios.post("/api/process-controller/approveReciverGtOgp", {
          ogpId: record.ogpId,
        },
      {params: {actionby:userId}});
        message.success("Gate Pass Receiver Approved Successfully");
      } else if (role === "Store Purchase Officer") {
        // Final GT Approval
        await axios.post("/api/process-controller/approveGtOgp", {
          ogpId: record.ogpId,
        }, {params:{actionBy: userId}});
        message.success("Gate Pass Final Approved Successfully");
      }
      fetchGatePassData();
      return;
    }

    if (record.formType === "IGP") {
      try {
        await axios.post("/api/process-controller/approveMaterialIgp", {
          igpId: record.igpId,
        }, {params: {actionBy:userId}});
        message.success("Gate Pass Approved Successfully");
        fetchGatePassData();
      } catch (error) {
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to approve Gate Pass"
        );
        console.error(error);
      }
      return;
    }
    if (record?.formType === "GI") {
      try {
        await axios.post("/api/process-controller/approveGiOgp", {
          ogpId: record.ogpId,
        }, {params: {actionBy:userId}});
        message.success("Gate Pass Approved Successfully");
        fetchGatePassData();
      } catch (error) {
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to approve Gate Pass"
        );
        console.error(error);
      }
      return;
    }
    if (record.formType === "ASSET_DISPOSAL") {
    try {
      await axios.post(`/api/process-controller/approveOgpAssetDisposal?disposalOgpId=${record.disposalOgpId}`, null , {params: {actionBy:userId}});
      message.success("Asset Disposal Approved Successfully");
      fetchGatePassData(); // refresh after approval
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to approve Asset Disposal");
      console.error(error);
    }
    return;
  }
    
    try {
      await axios.post(
        `/api/process-controller/approveOgp?processNo=${
          "INV/" + record.ogpSubProcessId
        }`,
        {
          processNo: "INV/" + record.ogpSubProcessId,
          type: record?.issueNoteId ? "ISN" : "PO",
        },  {params: {actionBy:userId}}
      );
      message.success("Gate Pass approved successfully");
      fetchGatePassData(); // Refresh the data
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to approve Gate Pass"
      );
    }
  };

  const navigate = useNavigate();

  const handleCreateIGP = async (record) => {
    navigate("/inventory/inward", {
      state: {
        processNo: `INV/${record.ogpSubProcessId}`,
        type: record?.issueNoteId ? "Goods Issue" : "PO",
      },
    });
  };

  const handleReject = async (record) => {
    if(record.formType === "GT"){
      try{
        await axios.post("/api/process-controller/rejectGtOgp", {ogpId: record.ogpId}, {params: {actionBy:userId}})
        message.success("Gate Pass Rejected Successfully");
        fetchGatePassData();
      }
      catch(error){
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to reject Gate Pass"
        );
      }
      return
    }
    if (record.formType === "IGP") {
      try {
        await axios.post("/api/process-controller/rejectMaterialIgp", {
          igpId: record.igpId,
        } , {params: {actionBy:userId}});
        message.success("Gate Pass Rejected Successfully");
        fetchGatePassData();
      } catch (error) {
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to approve Gate Pass"
        );
        console.error(error);
      }
      return;
    }
    if (record?.formType === "GI") {
      try {
        await axios.post("/api/process-controller/rejectGiOgp", {
          ogpId: record.ogpId,
        }, {params: {actionBy:userId}});
        message.success("Gate Pass Rejected Successfully");
        fetchGatePassData();
      } catch (error) {
        message.error(
          error?.response?.data?.responseStatus?.message ||
            "Failed to approve Gate Pass"
        );
        console.error(error);
      }
      return;
    }
     if (record.formType === "ASSET_DISPOSAL") {
    try {
      await axios.post(`/api/process-controller/rejectOgpAssetDisposal?disposalOgpId=${record.disposalOgpId}`, null, {params: {actionBy:userId}});
      message.success("Asset Disposal Rejected Successfully");
      fetchGatePassData(); // refresh after rejection
    } catch (error) {
      message.error(error?.response?.data?.responseStatus?.message || "Failed to reject Asset Disposal");
      console.error(error);
    }
    return;
  }
    try {
      await axios.post(`/api/process-controller/rejectOgp`, {
        processNo: "INV/" + record.ogpSubProcessId,
        type: record?.issueNoteId ? "ISN" : "PO",
      } , {params: {actionBy:userId}});
      message.success("Gate Pass rejected successfully");
      fetchGatePassData();
    } catch (error) {
      message.error(
        error?.response?.data?.responseStatus?.message ||
          "Failed to reject Gate Pass"
      );
    }
  };

  const { materialMaster, locationMaster, userMaster, locatorMaster } =
    useSelector((state) => state.masters) || [];

      const locatorMasterObj = locatorMaster?.reduce((acc, obj) => {
    const { value, label } = obj;
    acc[value] = label;
    return acc;
  }, {});

    const locationMasterObj = locationMaster?.reduce((acc, obj) => {
      acc[obj?.locationCode] = obj.locationName;
      return acc;
    }, {});

  const columns = [
    // {
    //   title: 'Issue Note ID',
    //   dataIndex: 'issueNoteId',
    //   key: 'issueNoteId',
    //   searchable: true,
    //   fixed: 'left',
    //   render: (text) => text ? "INV/"+text : ""
    // },
    {
      title: "Goods Transfer ID",
      dataIndex: "gtId",
      key: "gtId",
      searchable: true,
      fixed: "left",
    }, 
    {
      title: "Sender Field Station",
      dataIndex: "senderLocationId",
      render: (text) => locationMasterObj[text]
    },
    {
      title: "Receiver Field Station",
      dataIndex: "receiverLocationId",
      render: (text) => locationMasterObj[text]
    },
    {
      title: "Goods Inspection ID",
      dataIndex: "giId",
      key: "giId",
      searchable: true,
      fixed: "left",
    },
    {
      title: "OGP ID",
      dataIndex: "ogpSubProcessId",
      key: "ogpSubProcessId",
      searchable: true,
      render: (text, record) =>
        (record?.formType === "GI" || record?.formType === "GT")
          ? record.ogpId
          : record?.ogpId
          ? "INV/" + text
          : null,
    },
    {
      title: "IGP ID",
      dataIndex: "igpSubProcessId",
      key: "igpSubProcessId",
      searchable: true,
      render: (text, record) =>
        text ? "INV/" + text : record?.igpId ? record.igpId : null,
    },
    {title : "Ogp Asset Disposal Id",
      dataIndex: "disposalOgpId",
      key: "disposalOgpId",
      searchable: true,
      render: (text, record) =>
        text ? "INV/" + text : record?.disposalOgpId ? record.disposalOgpId : null,

    },
    {
      title: "PO ID",
      dataIndex: "poId",
      key: "poId",
      searchable: true,
      render: (text, record) => {
        const giId = record?.giId;

        console.log("JFHHJGF: ", record?.giId, record?.giId?.substring(0, 3));

        if (
          giId &&
          giId.substring(0, 3) === "INV" &&
          giId.split("/").length === 2
        ) {
          return "PO" + giId.split("/")[0].substring(3);
        }

        if (text) {
          return text;
        }

        return null;
      },
    },
    {
      title: "Approval Status",
      dataIndex: "status",
      key: "status",
      // searchable: true
    },
    {
      title: "Sender Name",
      dataIndex: "senderName",
      key: "senderName",
    },
    {
      title: "Receiver Name",
      dataIndex: "receiverName",
      key: "receiverName",
    },
    {
      title: "OGP Type",
      dataIndex: "ogpType",
      key: "ogpType",
    },
    {
      title: "Material Details",
      dataIndex: "details",
      key: "details",
      render: (details) => (
        <Table
          dataSource={details}
          pagination={false}
          columns={[
            { title: "Detail ID", dataIndex: "detailId", key: "detailId" },
            {
              title: "Material Code",
              dataIndex: "materialCode",
              key: "materialCode",
            },
            {
              title: "Material Description",
              dataIndex: "materialDesc",
              key: "materialDesc",
            },
             { title: "Asset Code", dataIndex: "assetCode", key: "assetCode" },
            { title: "Asset ID", dataIndex: "assetId", key: "assetId" },
             { title: "Serial No", dataIndex: "serialNo", key: "serialNo" },
            {
              title: "Asset Description",
              dataIndex: "assetDesc",
              key: "assetDesc",
            },
            {title: "Sender Locator", dataIndex: 'senderLocatorId', render: (text) => locatorMasterObj[text]},
            {title: "Receiver Locator", dataIndex: 'receiverLocatorId', render: (text) => locatorMasterObj[text]},
            { title: "Locator ID", dataIndex: "locatorId", key: "locatorId" },
            // { title: 'UOM', dataIndex: 'uomId', key: 'uomId' },
            { title: "Quantity", dataIndex: "quantity", key: "quantity" },
            // { title: 'Type', dataIndex: 'type', key: 'type' }
          ]}
        />
      ),
    },
    ...(role === "Store Purchase Officer" || role === "Indent Creator"
      ? [
          {
            title: "Actions",
            key: "actions",
            fixed: "right",
            render: (_, record) => {
              if (record.status === "AWAITING APPROVAL" || record.status === "PENDING RECEIVER APPROVAL" || record.status === "RECEIVER APPROVED") {
                return (
                  <Space>
                    <Button
                      type="primary"
                      onClick={() => handleApprove(record)}
                    >
                      Approve
                    </Button>
                    <Button danger onClick={() => handleReject(record)}>
                      Reject
                    </Button>
                    {/* <Popover
                content={
                  <div style={{ padding: 12 }}>
                    <Input.TextArea
                      placeholder="Reject Comments"
                      rows={3}
                      value={rejectComment}
                      onChange={(e) => setRejectComment(e.target.value)}
                    />
                    <Button
                      type="primary"
                      onClick={() => handleReject(record)}
                      style={{ marginTop: 8 }}
                    >
                      Submit
                    </Button>
                  </div>
                }
                title="Reject"
                trigger="click"
              >
                <Button danger>
                  Reject
                </Button>
              </Popover> */}
                  </Space>
                );
              }

              if (record.status === "APPROVED" && !record.igpSubProcessId) {
                return (
                  <Button
                    type="primary"
                    onClick={() => handleCreateIGP(record)}
                  >
                    Create IGP
                  </Button>
                );
              }

              return null;
            },
          },
        ]
      : []),
  ];

  const [dataSource, setDataSource] = useState([]);
/*
  const fetchGatePassData = async () => {
    try {
      const { data } = await axios.get(
        "/api/process-controller/getGatePassReport"
      );
      const { data: data1 } = await axios.get(
        "/api/process-controller/getAwaitingRejectedGi"
      );
      const { data: data2 } = await axios.get(
        "/api/process-controller/getPendingIgp"
      );

      const {data: data3} = await axios.get(
        "/api/process-controller/getPendingGtOgp"
      );
      console.log("Data3.responseData: ", data3.responseData);
      const dataCopy = data1.responseData?.map((item) => ({
        ...item,
        formType: "GI",
        status: "AWAITING APPROVAL",
        details: item.materialDtlList.map((subitem) => ({
          ...subitem,
          quantity: subitem.rejectedQuantity,
        })),
      }));
      const dataCopy2 = data2.responseData?.map((item) => ({
        ...item,
        formType: "IGP",
        details: item.materialDtlList.map((subitem) => ({
          ...subitem,
          materialDesc: subitem.description,
          detailId: subitem.id,
        })),
      }));

      const data3Copy = data3.responseData?.map((item) => ({
        ...item,
        ogpId: item.id,
        formType: "GT",
        details: item.materialDtlList
      }));
      // setDataSource([...data?.responseData, ...dataCopy, ...dataCopy2] || []);
      setDataSource(data3Copy)
      // setDataSource(dataCopy2 || []);
      // setDataSource([...dataCopy] || []);
    } catch (error) {
      message.error("Error fetching gate pass details.");
      console.error(error);
    }
  };*/
  const fetchGatePassData = async () => {
    try {
      const { data } = await axios.get(
        "/api/process-controller/getGatePassReport"
      );
      const { data: data1 } = await axios.get(
        "/api/process-controller/getAwaitingRejectedGi"
      );
      const { data: data2 } = await axios.get(
        "/api/process-controller/getPendingIgp"
      );
      const { data: data4 } = await axios.get("/api/process-controller/pendingOgpAssetDisposal");
    /*  const {data: data3} = await axios.get(
        "/api/process-controller/getPendingGtOgp"
      );*/
       let data3 = { responseData: [] }; // default empty
    if (role === "Indent Creator") {
    //  const { data: resp } = await axios.get("/api/process-controller/getRecevierPendingGtOgp");
      const { data: resp } = await axios.get(
        `/api/process-controller/getRecevierPendingGtOgp?userId=${userId}` // 👈 userId passed here
      );
      data3 = resp;
    } else if (role === "Store Purchase Officer") {
      const { data: resp } = await axios.get("/api/process-controller/getPendingGtOgp");
      data3 = resp;
    }
      console.log("Data3.responseData: ", data3.responseData);
      const dataCopy = data1.responseData?.map((item) => ({
        ...item,
        formType: "GI",
        status: "AWAITING APPROVAL",
        details: item.materialDtlList.map((subitem) => ({
          ...subitem,
          quantity: subitem.rejectedQuantity,
        })),
      }));
      const dataCopy2 = data2.responseData?.map((item) => ({
        ...item,
        formType: "IGP",
        details: item.materialDtlList.map((subitem) => ({
          ...subitem,
          materialDesc: subitem.description,
          detailId: subitem.id,
        })),
      }));

      const data3Copy = data3.responseData?.map((item) => ({
        ...item,
        ogpId: item.id,
        formType: "GT",
        details: item.materialDtlList
      }));
       const data4Copy = data4.responseData?.map((item) => ({
      ...item,
      formType: "ASSET_DISPOSAL",
      status: "AWAITING APPROVAL",
      details: item.assets
    }));
      setDataSource([...data?.responseData, ...dataCopy, ...dataCopy2, ...data3Copy , ...data4Copy] || []);
      // setDataSource(data3Copy)
      // setDataSource(dataCopy2 || []);
      // setDataSource([...dataCopy] || []);
    } catch (error) {
      message.error("Error fetching gate pass details.");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchGatePassData();
  }, []);

  return (
    <div>
      <TableComponent
        dataSource={dataSource}
        columns={columns}
        storageKey="GatePassQueue_REPORT_COLUMNS"
      />
    </div>
  );
};

export default GatePass;
