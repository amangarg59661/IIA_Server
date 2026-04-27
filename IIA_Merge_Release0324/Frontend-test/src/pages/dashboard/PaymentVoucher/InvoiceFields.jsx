import { handleSearch } from "../../../utils/CommonFunctions";

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

export const invoiceFields =(formData, poOptions, grnIds,setSelectedPoId, soOptions)=> [
     {
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
         {
          name: "grnNumber",
          label: "GRN Number",
          type: "select",
          required: true,
          options: grnIds,
        },
      ]
    : []),
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
    {
        name: "paymentVoucherType",
        label: "Payment Voucher Type",
        type: "select",
        required:true,
         options: [
            { value: "Advance", label: "Advance" },
            { value: "Partial", label: "Partial" },
            { value: "Full Payment", label: "Full Payment" },
        ],

      },
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
        disabled: true,
        required: true,
      },
      {
        name: "vendorInvoiceDate",
        label: "Vendor Invoice Date",
        type: "date",
        disabled: true,
      },
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
        label: "Total Amount Payable",
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
  label: "TDS Amount",
  name: "tdsAmount",
  type: "text",
  value: formData.tdsAmount,
  
},
{
  label: "Payment Voucher Amount",
  name: "paymentVoucherNetAmount",
  type: "text",
  disabled: true, // auto-calculated
  value: formData.netAmount
},
   ]},
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

