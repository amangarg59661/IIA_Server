import { Button, Popover, Table, Input } from 'antd';
import React, { useState, useRef } from 'react';
import { useSelector } from 'react-redux';
import { convertToCurrency, handleSearch, updateFormData } from '../utils/CommonFunctions';

const { Search } = Input;

const MaterialSearch = ({ customCols, itemsArray, setFormData }) => {
  const [selectedItems, setSelectedItems] = useState([]);
  const [filteredData, setFilteredData] = useState(itemsArray || []);
  const [tableOpen, setTableOpen] = useState(false);
  const [searchText, setSearchText] = useState("");

  const { locatorMaster } = useSelector(state => state.masters);

  const locatorMasterObj = locatorMaster?.reduce((acc, obj) => {
    const { value, label } = obj;
    acc[value] = label;
    return acc;
  }, {});
const debounceTimer = useRef(null);
  const handleSelectItem = (record) => {
    setTableOpen(false);

    const index = selectedItems.findIndex(item => item.materialCode === record.materialCode);
    if (index === -1) {
      setSelectedItems(prev => [...prev, record]);

      const newItem = {
        materialCode: record.materialCode,
        category: record.category,
        subCategory: record.subCategory,
        description: record.description,
        materialDesc: record.materialDesc,
        uom: record.uom,
        estimatedPriceWithCcy: record.estimatedPriceWithCcy,
        indigenousOrImported: record.indigenousOrImported,
        quantity: record?.quantity || 0,
        locatorId: record?.locatorId,
        senderLocatorId: record?.locatorId,
        unitPrice: record?.unitPrice || 0,
        depriciationRate: record?.depriciationRate || 0,
      };
      console.log("NEW ITEM: ", newItem);

      updateFormData(newItem, setFormData);
    } else {
      const updatedItems = [...selectedItems];
      updatedItems.splice(index, 1);
      setSelectedItems(updatedItems);
    }
  };

  const tableColumns = [
    {
      title: "Material Code",
      dataIndex: "materialCode",
      key: "materialCode",
      fixed: "left",
    },
    {
      title: "Category",
      dataIndex: "category",
      key: "category",
    },
    {
      title: "Sub Category",
      dataIndex: "subCategory",
      key: "subCategory",
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
    },
    {
      title: "UOM",
      dataIndex: "uom",
      key: "uom",
    },
    {
      title: "Estimated Price",
      dataIndex: "estimatedPriceWithCcy",
      key: "estimatedPriceWithCcy",
      render: (value) => convertToCurrency(value) || "N/A",
    },
    {
      title: "Imported?",
      dataIndex: "indigenousOrImported",
      key: "indigenousOrImported",
      render: (value) => (value ? "Imported" : "Indigenous"),
    },
    {
      title: "Action",
      key: "action",
      fixed: "right",
      render: (_, record) => (
        <Button
          onClick={() => handleSelectItem(record)}
          type={
            selectedItems?.some(item => item.materialCode === record.materialCode)
              ? "default"
              : "primary"
          }
        >
          {selectedItems?.some(item => item.materialCode === record.materialCode)
            ? "Deselect"
            : "Select"}
        </Button>
      ),
    },
  ];

  const actionCol =   {
      title: "Action",
      key: "action",
      fixed: "right",
      render: (_, record) => (
        <Button
          onClick={() => handleSelectItem(record)}
          type={
            selectedItems?.some(item => item.materialCode === record.materialCode)
              ? "default"
              : "primary"
          }
        >
          {selectedItems?.some(item => item.materialCode === record.materialCode)
            ? "Deselect"
            : "Select"}
        </Button>
      ),
    }

  const content = (
    <Table
      pagination={{ pageSize: 5 }}
      dataSource={filteredData}
      columns={customCols ? [...customCols, actionCol] : tableColumns}
      scroll={{ x: "max-content" }}
    //   style={{
    //     display: tableOpen ? "block" : "none",
    //   }}
      rowKey="materialCode"
    //   className='w-1/2'
    />
  );

  return (
    <div>
      <Popover
        className='pappu'
        content={content}
        title="Search Materials"
        trigger="click"
        open={tableOpen}
        onOpenChange={v => setTableOpen(v)}
        placement="right"
      >
        <Search
  placeholder="Search materials"
  value={searchText}
  onChange={(e) => {
    const value = e.target.value;
    setSearchText(value);

    // Debounce: filter in background, don't open popover
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      handleSearch(value, itemsArray, setFilteredData, setSearchText);
    }, 500);
  }}
  onSearch={() => setTableOpen(true)} // open only on icon click or Enter
  style={{ width: 200 }}
  allowClear
  onClear={() => {
    setSearchText("");
    setFilteredData(itemsArray);
    setTableOpen(false);
  }}
/>
      </Popover>
    </div>
  );
};

export default MaterialSearch;
