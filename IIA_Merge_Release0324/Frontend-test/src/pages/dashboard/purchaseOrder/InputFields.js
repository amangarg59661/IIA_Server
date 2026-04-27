import countryList from "react-select-country-list";

const countryOptions = [
  { label: "Austria", value: "Austria" },
  { label: "Australia", value: "Australia" },
  { label: "ARGENTINA", value: "ARGENTINA" },
  { label: "BELGIUM", value: "BELGIUM" }
];
/*const warrantyOptions = Array.from({ length: 20 }, (_, i) => {
  const year = i + 1;
  const label = `${year} Year${year > 1 ? "s" : ""}`;
  return {
    label: label,
    value: label
  };
});
*/
const warrantyOptions = [
  { label: "NA", value: "NA" },
  ...Array.from({ length: 20 }, (_, i) => {
    const year = i + 1;
    const label = `${year} Year${year > 1 ? "s" : ""}`;
    return {
      label: label,
      value: label
    };
  })
];



const typeOfSecurityOptions = [
    { label: "Bank Guarantee", value: "Bank Guarantee" },
    { label: "Fixed Deposit Receipt", value: "Fixed Deposit Receipt" },
    { label: "Account Payee Demand Draft", value: "Account Payee Demand Draft" },
    { label: "Account Payee Cheque", value: "Account Payee Cheque" },
    { label: "Insurance Surety Bonds", value: "Insurance Surety Bonds" },
    { label: "Indemnity Bond", value: "Indemnity Bond" },
    { label: "Online Payment", value: "Online Payment" },
    { label: "Others", value: "Others" }
];


export const PoDetails = [
   {
            heading: "Search PO",
            colCnt: 2,
            fieldList: [
        {
            name: "searchValue",
            label: "Search Value",
            type: "indentSearch",
      // formData.searchType === "submittedDate" ? "date" : "text"
        },
    ]
    },
     {
        heading: "Status",
        colCnt:2,
        fieldList:[
    {
        name: "processStage",
        label: "Process Stage",
        type: "text",
        disabled: true
    },
    {
        name: "status",
        label: "Status",
        type: "text",
        disabled: true
    }
        ]
    },
    {
      heading: "PO Search",
      colCnt: 4,
      fieldList: [{
        name: "poId",
        label: "PO ID",
       // type: "search",
        type: "select",
        span: 1
      }]
    },
    {
      heading: "Tender Details",
      colCnt: 4,
      fieldList: [
        {
          name: "tenderId",
          label: "Tender ID",
          type: "select",
          options: [],
          required: true
        },
        {
          name: "consignesAddress",
          label: "Consignee Address",
          type: "text",
        },
        {
          name: "billingAddress",
          label: "Billing Address",
          type: "text",
          required: true,
          span: 2
        },
       {
        name: "deliveryPeriod",
        label: "Delivery Period",
        type: "select",
        span: 2,
        options: [
          ...Array.from({ length: 20 }, (_, i) => ({
          label: `${i + 1} ${i + 1 === 1 ? "Week" : "Weeks"}`, // pluralize
          value: String(i + 1),
        })),
      ],
      },
        {
          name:"deliveryDate",
          label:"Delivery Date",
          type:"date",
          required:true
        }
      ]
    },
    {
      heading: "Material Details",
      name: "materialDtlList",
      colCnt: 4,
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
          name: "uom",
          label: "UOM",
          type: "text",
          disabled: true,
          required: true,
          span: 2
        },
        {
          name: "quantity",
          label: "Quantity",
          type: "text",
          required: true
        },
        {
          name: "rate",
          label: "Unit Rate",
          type: "text",
          required: true
        },
        {
          name: "currency",
          label: "Currency",
          type: "text",
          disabled: true,
          required: true
        },
        {
            name: "exchangeRate",
            label: "Exchange Rate",
            type: "text",
            span: 2
        },
        {
            name: "gst",
            label: "GST (%)",
            type: "select",
            required: true,
            options: [
              { label: "Nil", value: "0" },
              { label: "5%", value: "5" },
              { label: "12%", value: "12" },
              { label: "18%", value: "18" },
              { label: "28%", value: "28" }
            ]
        },
        {
            name: "duties",
            label: "Duties (%)",
            type: "text",
        },
        {
            name: "freightCharge",
            label: "Freight Charges",
            type: "text", 
            span: 3
        },
        {
            name: "inrEquivalent",
            label: "Equivalent INR",
            type: "text",
            disabled: true,
            span: 3
        }

      ]
    },
    {
      heading: "Purchase Details",
      colCnt: 4,
      fieldList: [
        {
          name: "warranty",
          label: "Warranty",
          //type: "text",
          type:"select",
          options:warrantyOptions,
        }, 
        {
          name: "ifLdClauseApplicable",
          label: "If LD Clause Applicable",
          type: "checkbox",
        },
        {
          name: "incoTerms",
          label: "Inco Terms",
          type: "text",  
        },
        {
          name: "paymentTerms",
          label: "Payment Terms",
          type: "text",
        },
        {
          name: "applicablePbgToBeSubmitted",
          label: "Applicable PBG to be Submitted",
          type: "select",
          span: 2,
          options: [
            ...Array.from({ length: 20 }, (_, i) => ({
            label: `${i + 1}%`,
            value: String(i + 1),
          })),
          { label: "NA", value: "NA" },
          ],
        },
         {
          name: "quotationNumber",
          label: "Quotation Number",
          type: "text",
          required: true,
         // options: [],
        },
       {
                    name: "buyBackAmount",
                    label: "Buy Back Amount",
                    type: "text",
                    required: true,
                },
         {
          name: "quotationDate",
          label: "Quotation Date",
          type: "date",
          required: true,
         // options: [],
        }, 
         {
          name: "additionalTermsAndConditions",
          label: "Additional Terms And Conditions(ATC)",
          type: "text",
         // required: true,
         // options: [],
        }, 

        {
            name: "transporterAndFreightForWarderDetails",
            label: "Freight Forwarder",
            type: "select",
            options: countryOptions,
            span: 2,
            required: false
        },
        {
                    name: "comparativeStatementFileName",
                    label: "Comparative Statement",
                    type: "multiImage",
                    span:2,
                    //required: true,
        },
         {
                    name: "gemContractFileName",
                    label: "Gem Contract Upload",
                    type: "multiImage",
                    span:2,
                    //required: true,
        }
      ]
    },
     {
      heading: "Performance And Warranty Security",
      colCnt: 3,
      fieldList: [
        {
          name: "typeOfSecurity",
          label: "Type Of Security",
          type: "select",
          options :typeOfSecurityOptions,
         // required: true,
         // options: [],
        }, 
        {
          name: "securityNumber",
          label: "Security Number",
          type: "text",
         // required: true,
         // options: [],
        }, 
        {
          name: "securityDate",
          label: "Security Date",
          type: "date",
        },
        {
          name: "expiryDate",
          label: "Expiry Date",
          type: "date",
        },
     ]
    },
    {
      heading: "Vendor Details",
      colCnt: 3,
      fieldList: [
        {
          name: "vendorName",
          label: "Vendor Name",
          type: "select",
         // required: true,
         // options: [],
        }, 
        {
          name: "vendorId",
          label: "Vendor ID",
          type: "select",
          required: true 
        },
        {
          name: "vendorAddress",
          label: "Vendor Address",
          type: "text",
          required: true,
         // disabled: true,
        },
        {
          name: "vendorAccountNumber",
          label: "Vendor A/C No.",
          type: "text",
          required: true,
        //  disabled: true,
        },
        {
            name: "vendorsIfscCode",
            label: "Vendor IFSC Code",
            type: "text",
            required: true,
           // disabled: true,
        },
        {
            name: "vendorAccountName",
            label: "Vendor A/C Name",
            type: "text",
            required: true,
          //  disabled: true,
        }
      ]
    }
  ];