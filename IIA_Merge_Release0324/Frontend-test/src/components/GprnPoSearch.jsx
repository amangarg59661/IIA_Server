import React, { useEffect, useState, useRef } from "react";
import { Modal, Table, Button, Input, Tag } from "antd";

const { Search } = Input;

const GprnPoSearch = ({ poArray = [], setFormData, handleSearch }) => {
  const [open, setOpen] = useState(false);
  const [filteredData, setFilteredData] = useState(poArray || []);
  const [searchText, setSearchText] = useState("");
  const [selectedPoId, setSelectedPoId] = useState(null);
const debounceTimer = useRef(null);
  // Keep filteredData in sync with poArray changes:
  useEffect(() => {
    setFilteredData(poArray || []);
  }, [poArray]);

 
  const handleSearchInput = (value) => {
  setSearchText(value);

  if (debounceTimer.current) clearTimeout(debounceTimer.current);

  debounceTimer.current = setTimeout(() => {
    const q = (value || "").toLowerCase();

    const filtered = (poArray || []).filter((po) => {
      const poText = `${po.poId} ${po.vendorName ?? ""} ${po.projectName ?? ""}`.toLowerCase();
      const indentText = (po.indentIds || []).join(" ").toLowerCase();
      const materialText = (po.materials || [])
        .map(
          (m) =>
            `${m.materialCode ?? ""} ${m.materialDesc ?? ""} ${m.orderQty ?? ""} ${m.receivedQty ?? ""} ${m.pendingQty ?? ""}`
        )
        .join(" ")
        .toLowerCase();

      return poText.includes(q) || indentText.includes(q) || materialText.includes(q);
    });

    setFilteredData(filtered); // only filter, don't open modal
  }, 500);
};
  // const handleSearchInput = (value) => {
  //   const q = (value || "").toLowerCase();
  //   setSearchText(value);

  //   // Filter logic
  //   const filtered = (poArray || []).filter((po) => {
  //     const poText = `${po.poId} ${po.vendorName ?? ""} ${po.projectName ?? ""}`.toLowerCase();
  //     const indentText = (po.indentIds || []).join(" ").toLowerCase();
  //     const materialText = (po.materials || [])
  //       .map(
  //         (m) =>
  //           `${m.materialCode ?? ""} ${m.materialDesc ?? ""} ${m.orderQty ?? ""} ${m.receivedQty ?? ""} ${m.pendingQty ?? ""}`
  //       )
  //       .join(" ")
  //       .toLowerCase();
  //     // This includes all requested fields!
  //     return (
  //       poText.includes(q) ||
  //       indentText.includes(q) ||
  //       materialText.includes(q)
  //     );
  //   });

  //   setFilteredData(filtered);
  //   // Modal open on search
  //   setOpen(true);
  // };

  // Table columns, same as before
  const columns = [
    {
      title: "PO ID",
      dataIndex: "poId",
      key: "poId",
      width: 120,
      fixed: "left",
    },
    {
      title: "Indentor Name",
      dataIndex: "indentIds",
      width: 200,
      render: (ids) =>
        ids?.length ? ids.map((id) => <Tag key={id}>{id}</Tag>) : "—",
    },
    {
      title: "Materials",
      dataIndex: "materials",
      width: 600,
      render: (materials = []) => (
        <Table
          dataSource={materials}
          rowKey={(row, i) => row.materialCode + i}
          pagination={false}
          size="small"
          columns={[
            { title: "Code", dataIndex: "materialCode", width: 120 },
            { title: "Description", dataIndex: "materialDesc", width: 250 },
            { title: "Qty", dataIndex: "orderQty", width: 80 },
            { title: "Received", dataIndex: "receivedQty", width: 80 },
            { title: "Pending", dataIndex: "pendingQty", width: 80 },
          ]}
        />
      ),
    },
   
{
      title: "Action",
      width: 150,
      render: (_, record) => {
        const disabled = selectedPoId && selectedPoId !== record.poId;

        return selectedPoId === record.poId ? (
          <Button danger onClick={handleDeselect}>Deselect</Button>
        ) : (
          <Button type="primary" disabled={disabled} onClick={() => handleSelect(record)}>
            Select
          </Button>
        );
      },
    },
  ];

 
  const handleSelect = (record) => {
    setSelectedPoId(record.poId);
    setFormData((prev) => ({ ...prev, poId: record.poId }));
    handleSearch(record.poId);
    setOpen(false);
  };

const handleDeselect = () => {
  setSelectedPoId(null);

  setFormData((prev) => ({
    ...prev,

    // Reset PO Fields
    poId: "",
    vendorId: "",
    vendorName: "",
    vendorEmail: "",
    vendorContactNo: "",
    project: "",
    indentorName: "",
    indentId: "",
    consigneeDetail: "",
    fieldStation: "",

    

    // Reset materials list
    materialDtlList: [],

    // Reset totals
    totalQuantity: "",
    gprnNo: ""
  }));
};


  return (
    <>
      {/* <Search
        placeholder="Search PO, Material, Indent…"
        value={searchText}
        onChange={(e) => handleSearchInput(e.target.value)}
        onClick={() => setOpen(true)}
        style={{ width: 280 }}
       
        onSearch={handleSearchInput} // This opens modal on pressing enter!
        allowClear
      /> */}
<Search
  placeholder="Search PO, Material, Indent…"
  value={searchText}
  onChange={(e) => handleSearchInput(e.target.value)}
  onClick={() => setOpen(true)}
  style={{ width: 280 }}
  onSearch={() => setOpen(true)} // opens modal on icon click or Enter
  allowClear
/>
      <Modal
        open={open}
        title="Search Purchase Orders"
        footer={null}
        onCancel={() => setOpen(false)}
        width={1300}
      >
        <Table
          dataSource={filteredData}
          columns={columns}
          rowKey="poId"
          pagination={{ pageSize: 5 }}
          scroll={{ x: 1200 }}
        />
      </Modal>
    </>
  );
};

export default GprnPoSearch;
