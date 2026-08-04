import { handleSearch } from "../../../utils/CommonFunctions";
import { Select, Input, Button, Row, Col, Tag, Space } from "antd";
import { PlusOutlined, MinusCircleOutlined } from "@ant-design/icons";

export const locatorMaster = [
    {
        value: "1",
        label: "Locator 1"
    },
    {
        value: "2",
        label: "Locator 2"
    },
    {
        value: "3",
        label: "Locator 3"
    },
    {
        value: "4",
        label: "Locator 4"
    },
]

export const invoiceFields =(formData, poOptions, grnIds,setSelectedPoId, soOptions, cpOptions, setFormData, tdsSectionDropdown, recomputeNetAmount, handleSearchVoucher, searchDone)=> [
     { heading: "Search Payment Voucher",
    colCnt: 2,
    fieldList: [
      {
        name: "searchValue",
        label: "Payment Voucher Number",
        type: "custom",
        disabled: true,
        render: () => (
          <Input.Search
            placeholder="e.g. GRN45/12, PO123/5"
            value={formData.searchValue || ""}
            onChange={(e) => setFormData(prev => ({ ...prev, searchValue: e.target.value }))}
            onSearch={() => handleSearchVoucher()}
            enterButton="Search"
            allowClear
          />
        ),
      },
    ],
  },
  ...(searchDone
    ? [
        {
          heading: "Status",
          colCnt: 4,
          fieldList: [
            {
              name: "voucherStatusTags",
              type: "custom",
              disabled: true,
              render: () => (
                <Space>
                  <Tag color={formData.status === "Approved" ? "green" : formData.status === "Rejected" ? "red" : "gold"}>
                    {formData.status || "Draft"}
                  </Tag>
                  {formData.paymentVoucherType && <Tag color="blue">{formData.paymentVoucherType}</Tag>}
                  {formData.paymentVoucherNumber && <Tag>{formData.paymentVoucherNumber}</Tag>}
                </Space>
              ),
            },
          ],
        },
      ]
    : []),{
    heading: "Invoice Details",
    colCnt: 2,
    fieldList: [
      {
        name: "paymentVoucherNumber",
        label: "Payment Voucher Number",
        type: "text",
       
      },
      {
        name: "paymentVoucherDate",
        label: "Payment Voucher Date",
        type: "date",
        required: true,
      },
      {
        name: "paymentVoucherIsFor",
        label: "Payment Voucher Is For",
        type: "select",
        required: true,
        options: [
            { value: "Purchase Order", label: "Purchase Order" },
            { value: "Service Order", label: "Service Order" },
            {value: "CP", label: "Contengency Purchase"},
        ],
      },

       ...(formData.paymentVoucherIsFor === "Purchase Order"
    ? [
        {
          name: "purchaseOrderids",
          label: "Purchase Order Ids",
          type: "pvselect",
          required: true,
          options: poOptions,
           showSearch: true,
  filterOption: (input, option) =>
    option.searchText.includes(input.toLowerCase()),
        },
        ...(formData.paymentVoucherType !== "Advance"
          ? [
              {
                name: "grnNumber",
                label: "GRN Number",
                type: "select",
                required: true,
                options: grnIds,
              },
            ]
          : []),
      ]
    : []),
  //    ...(formData.paymentVoucherIsFor === "Purchase Order"
  //   ? [
  //       {
  //         name: "purchaseOrderids",
  //         label: "Purchase Order Ids",
  //         type: "pvselect",
  //         required: true,
  //         options: poOptions,
  //          showSearch: true,
  // filterOption: (input, option) =>
  //   option.searchText.includes(input.toLowerCase()),
  //       },
  //        {
  //         name: "grnNumber",
  //         label: "GRN Number",
  //         type: "select",
  //         required: true,
  //         options: grnIds,
  //       },
  //     ]
  //   : []),
     ...(formData.paymentVoucherIsFor === "Service Order"
    ? [
        {
          name: "ServiceOrderDetails",
          label: "Service Order Ids",
          type: "select",
          required: true,
          options: soOptions,
        },
      ]
    : []),
    ...(formData.paymentVoucherIsFor === "CP"
    ? [
        {
          name: "cpDetails",
          label: "Contingency Purchase Ids",
          type: "select",
          required: true,
          options: cpOptions,
        }
      ]
    : []),
    {
        name: "paymentVoucherType",
        label: "Payment Voucher Type",
        type: "select",
        required:true,
         options: formData.paymentVoucherIsFor === "CP"
           ? [
               { value: "Partial", label: "Partial" },
               { value: "Full Payment", label: "Full Payment" },
             ]
           : [
               { value: "Advance", label: "Advance" },
               { value: "Partial", label: "Partial" },
               { value: "Full Payment", label: "Full Payment" },
             ],

      },
    // {
    //     name: "paymentVoucherType",
    //     label: "Payment Voucher Type",
    //     type: "select",
    //     required:true,
    //      options: [
    //         { value: "Advance", label: "Advance" },
    //         { value: "Partial", label: "Partial" },
    //         { value: "Full Payment", label: "Full Payment" },
    //     ],

    //   },
       {
        name: "vendorName",
        label: "Vendor Name",
        type: "text",
        disabled: true,
      },

      {
        name: "vendorInvoiceNumber",
        label: "Vendor Invoice Number",
        type: "text",
        disabled: !((formData.paymentVoucherIsFor === "Purchase Order" || formData.paymentVoucherIsFor === "Service Order") && formData.paymentVoucherType === "Advance"),
        required: true,
      },
      {
        name: "vendorInvoiceDate",
        label: "Vendor Invoice Date",
        type: "date",
        disabled: !((formData.paymentVoucherIsFor === "Purchase Order" || formData.paymentVoucherIsFor === "Service Order") && formData.paymentVoucherType === "Advance"),
        required: true,
      },
      // {
      //   name: "vendorInvoiceNumber",
      //   label: "Vendor Invoice Number",
      //   type: "text",
      //   disabled: true,
      //   required: true,
      // },
      // {
      //   name: "vendorInvoiceDate",
      //   label: "Vendor Invoice Date",
      //   type: "date",
      //   disabled: true,
      // },
      {
        name: "currency",
        label: "Currency",
        type: "text",
        disabled: true,
        required: true,
      },
      {
        name: "exchangeRate",
        label: "Exchange Rate",
        disabled: true,
        type: "text",
      },
     /* {
        name: "status",
        label: "Status",
        type: "text",
      },*/
       {
        name: "remarks",
        label: "Remarks",
        type: "text",
        span: 2,
      },  {
        name: "totalAmount",
        label: "Total Amount Payable (INR)",
        type: "text",
        disabled: true,
        span: 2,
      },
      
      ...(formData.paymentVoucherType === "Advance"
        ? [
            {
              name: "advanceAmount",
              label: "Advance Amount",
              type: "text",
              required: true,
             // disabled: true,
            },
           /* {
              name: "advanceRemarks",
              label: "Advance Remarks",
              type: "text",
            },*/
          ]
        : []),

      
      ...(formData.paymentVoucherType === "Partial"
        ? [
            {
              name: "partialAmount",
              label: "Partial Amount",
              type: "text",
              required: true,
            },
           /* {
              name: "pendingAmount",
              label: "Pending Amount",
              type: "text",
            },*/
          ]
        : []),
      ...(formData.paymentVoucherType === "Partial" && (formData.partialAmount || formData.partialBalanceAmount)
  ? [
      formData.partialAmount
        ? {
            name: "partialAmount",
            label: "Already Paid (Partial Amount)",
            type: "text",
            disabled: true,
          }
        : null,
      formData.partialBalanceAmount
        ? {
            name: "partialBalanceAmount",
            label: "Balance Amount (Partial)",
            type: "text",
            disabled: true,
          }
        : null,
    ].filter(Boolean) 
  : []),


...(formData.paymentVoucherType === "Advance"
    ? [
        {
          // name: "advanceAmount",
          // Modified by Aman
          name: "advanceAmountpaid",
          // End
          label: "Already Paid (Advance Amount)",
          type: "text",
          disabled: true,
        },
        {
          name: "advanceBalanceAmount",
          label: "Balance Amount (Advance)",
          type: "text",
          disabled: true,
        },
      ]
    : []),

    ],
  },
  {
    heading: "Voucher Amount Deatails",
    colCnt: 2,
    fieldList: [
{
  label: "Payment Voucher Amount",
  name: "paymentVoucherNetAmount",
  type: "text",
  disabled: true, // auto-calculated
  value: formData.netAmount
},
   ]},
   {
    heading: "TDS Details",
    colCnt: 23,
    fieldList: [
      {
        name: "tdsDtlListCustom",
        type: "custom",
        disabled: true,
        render: () => (
          <div style={{ width: "100%" }}>
            {(formData.tdsDtlList || []).map((row, idx) => (
               <div style={{ display: "flex", gap: 8, marginBottom: 8, alignItems: "center" }}>
                <div style={{ flex: "0 0 40%", minWidth: 220  }}>
                  <Select
                    placeholder="TDS Section / Type"
                    style={{ width: "100%" }}
                    value={row.tdsSection || undefined}
                    options={tdsSectionDropdown}
                    onChange={(val) => {
                      const list = [...(formData.tdsDtlList || [])];
                      list[idx] = { ...list[idx], tdsSection: val };
                      setFormData(prev => {
                        const updated = { ...prev, tdsDtlList: list };
                        updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
                        return updated;
                      });
                    }}
                  />
                </div>
                <div style={{ flex: "0 0 25%", minWidth: 140}}>
                  <Input
                    placeholder="Amount"
                    value={row.tdsAmount || ""}
                    onChange={(e) => {
                      const list = [...(formData.tdsDtlList || [])];
                      list[idx] = { ...list[idx], tdsAmount: e.target.value };
                      setFormData(prev => {
                        const updated = { ...prev, tdsDtlList: list };
                        updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
                        return updated;
                      });
                    }}
                  />
                </div>
                <div style={{ flex: "1 1 auto" , minWidth: 280}}>
                  <Input
                    placeholder="Remarks"
                    value={row.remarks || ""}
                    onChange={(e) => {
                      const list = [...(formData.tdsDtlList || [])];
                      list[idx] = { ...list[idx], remarks: e.target.value };
                      setFormData(prev => ({ ...prev, tdsDtlList: list }));
                    }}
                  />
                </div>
                <div style={{ flex: "0 0 32px" }}>
                  <Button
                    danger
                    type="text"
                    icon={<MinusCircleOutlined />}
                    onClick={() => {
                      const list = (formData.tdsDtlList || []).filter((_, i) => i !== idx);
                      setFormData(prev => {
                        const updated = { ...prev, tdsDtlList: list };
                        updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
                        return updated;
                      });
                    }}
                  />
                </div>
              </div>
            ))}
            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={() => setFormData(prev => ({ ...prev, tdsDtlList: [...(prev.tdsDtlList || []), {}] }))}
            >
              Add TDS Line
            </Button>
          </div>
        ),
      },
    ],
  },
   {
    heading: "Deductions",
    colCnt: 1,
    fieldList: [
      {
        name: "deductionDtlListCustom",
        type: "custom",
        disabled: true,
        render: () => (
          <div style={{ width: "100%" }}>
            {(formData.deductionDtlList || []).map((row, idx) => (
               <div style={{ display: "flex", gap: 8, marginBottom: 8, alignItems: "center" }}>
                <div style={{ flex: "0 0 35%", minWidth: 200 }}>
                  <Input
                    placeholder="Deduction Name"
                    value={row.deductionName || ""}
                    onChange={(e) => {
                      const list = [...(formData.deductionDtlList || [])];
                      list[idx] = { ...list[idx], deductionName: e.target.value };
                      setFormData(prev => ({ ...prev, deductionDtlList: list }));
                    }}
                  />
                </div>
                <div style={{ flex: "0 0 25%", minWidth: 140  }}>
                  <Input
                    placeholder="Amount"
                    value={row.deductionAmount || ""}
                    onChange={(e) => {
                      const list = [...(formData.deductionDtlList || [])];
                      list[idx] = { ...list[idx], deductionAmount: e.target.value };
                      setFormData(prev => {
                        const updated = { ...prev, deductionDtlList: list };
                        updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
                        return updated;
                      });
                    }}
                  />
                </div>
                <div style={{ flex: "1 1 auto" , minWidth: 280}}>
                  <Input
                    placeholder="Reason / Remarks"
                    value={row.remarks || ""}
                    onChange={(e) => {
                      const list = [...(formData.deductionDtlList || [])];
                      list[idx] = { ...list[idx], remarks: e.target.value };
                      setFormData(prev => ({ ...prev, deductionDtlList: list }));
                    }}
                  />
                </div>
                <div style={{ flex: "0 0 32px" }}>
                  <Button
                    danger
                    type="text"
                    icon={<MinusCircleOutlined />}
                    onClick={() => {
                      const list = (formData.deductionDtlList || []).filter((_, i) => i !== idx);
                      setFormData(prev => {
                        const updated = { ...prev, deductionDtlList: list };
                        updated.paymentVoucherNetAmount = recomputeNetAmount(updated);
                        return updated;
                      });
                    }}
                  />
                </div>
              </div>
            ))}
            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={() => setFormData(prev => ({ ...prev, deductionDtlList: [...(prev.deductionDtlList || []), {}] }))}
            >
              Add Deduction Line
            </Button>
          </div>
        ),
      },
    ],
  },
//    {
//     heading: "Voucher Amount Deatails",
//     colCnt: 2,
//     fieldList: [
//   {
//   label: "TDS Amount",
//   name: "tdsAmount",
//   type: "text",
//   value: formData.tdsAmount,
  
// },
// {
//   label: "Payment Voucher Amount",
//   name: "paymentVoucherNetAmount",
//   type: "text",
//   disabled: true, // auto-calculated
//   value: formData.netAmount
// },
//    ]},
   /*  {
    heading: "Purchase Order Details",
    name: "poDtlList",
    colCnt: 4,
    children: [
      { name: "purchaseOrderAmount", label: "Purchase Order Amount (Rs)", type: "text", required: true },
      { name: "advanceAmount", label: "Advance Amount", type: "text", required: true },
      { name: "advancePaid", label: "Advance Paid (Rs)", type: "text" },
      { name: "alreadyInvoicedAmount", label: "Already Invoice Amount (Rs)", type: "text" },
      { name: "balanceAmount", label: "Balance Amount (Rs)", type: "text" },
    ],
  },*/ {
      heading: "Material Details",
      name: "materialDtlList",
      colCnt: 6,
      children: [
        {
          name: "materialCode",
          label: "Material Code",
          type: "text",
          disabled: true,
          required: true,
          span: 2
        },
        {
          name: "materialDescription",
          label: "Material Description",
          type: "text",
          disabled: true,
          required: true,
          span: 2
        },
        {
          name: "quantity",
          label: "Quantity",
          type: "text",
          disabled: true,
          required: true
        },
        {
          name: "rate",
          label: "Unit Rate",
          type: "text",
          disabled: true,
          required: true
        },
        {
          name: "currency",
          label: "Currency",
          type: "text",
          disabled: true,
          required: true
        },
          // {
          //     name: "exchangeRate",
          //     label: "Exchange Rate",
          //     type: "text",
          //     disabled: true,
          //     span: 2
          // },
        {
            name: "gst",
            label: "GST (%)",
            type: "text",
            disabled: true,
            required: true
        },  {
            name: "amount",
            label: "Amount",
            type: "text",
            disabled: true,
            required: true
        },
      ]
    },
   /* {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 8,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 3,
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                // required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 3,
                // required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator",
                type: "select",
                options: locatorMaster,
                span: 2,
                required: true
            },
            {
                name: "unitPrice",
                label: "Unit Price",
                type: "text",
                required: true
            },...(formData.isDepreciationDisabled ? [] : [{
                name: "depriciationRate",
                label: "Depreciation Rate",
                type: "text",
                required: true
            }]),
            {
                name: "bookValue",
                label: "Book Value",
                type: "text",
                required: true,
                disabled: true,
            
            },
            
            {
                name: "receivedQuantity",
                label: "Received Quantity",
                type: "text",
                required: true
            },
            {
                name: "acceptedQuantity",
                label: "Accepted Quantity",
                type: "text",
                required: true
            },
        ]
    },*/
   /* {
    heading: "Vendor Details",
    name: "vendorDtlList",
    colCnt: 4,
    children: [
      { name: "vendorCode", label: "Vendor Code", type: "text", required: true },
      { name: "vendorName", label: "Vendor Name", type: "text", required: true },
      { name: "gstNumber", label: "GST Number", type: "text" },
      { name: "contactPerson", label: "Contact Person", type: "text" },
      { name: "contactNumber", label: "Contact Number", type: "text" },
      { name: "email", label: "Email", type: "text" },
    ],
  },*/
   
];

