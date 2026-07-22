const countryOptions = [
  { label: "Austria", value: "Austria" },
  { label: "Australia", value: "Australia" },
  { label: "ARGENTINA", value: "ARGENTINA" },
  { label: "BELGIUM", value: "BELGIUM" }
];

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

export const SoDetails = [
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
      heading: "SO Search",
      colCnt: 4,
      fieldList: [{
        name: "soId",
        label: "SO ID",
        type: "search",
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
          name: "jobCompletionPeriod",
          label: "Job Completion Period (in days)",
          type: "text",
          required: true
        },
        {
          name: "deliveryPeriod",
          label: "Delivery Period",
          type: "select",
          span: 2,
          options: [
            ...Array.from({ length: 20 }, (_, i) => ({
              label: `${i + 1} ${i + 1 === 1 ? "Week" : "Weeks"}`,
              value: String(i + 1),
            })),
          ],
        },
        {
          name:"deliveryDate",
          label:"Delivery Date",
          type:"date",
          required:true
        },
        {
          name:"startDateAmc",
          label:"Start Date Of AMC",
          type:"date",
          required:true
        },
        {
          name:"endDateAmc",
          label:"End Date Of AMC",
          type:"date",
          required:true
        }
      ]
    },
    {
      heading: "Job/Service Details",
      name: "jobDtlList",
      colCnt: 6,
      children: [
        {
          name: "jobCode",
          label: "Job Code",
          type: "text",
          disabled: true,
          required: true,
          span: 2
        },
        {
          name: "jobDescription",
          label: "Job Description",
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
          name: "budgetCode",
          label: "Budget Code",
          type: "text",
          span: 2,
          required: true
        },
        {
          name: "exchangeRate",
          label: "Exchange Rate",
          type: "text",
           shouldShow: (formData, childIndex) =>
    (formData?.jobDtlList?.[childIndex]?.currency || "").toString().toUpperCase() !== "INR",
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
          required: true
        },
        {
          name: "inrEquivalent",
          label: "Unit Price in INR",
          type: "text",
          disabled: true,
          span: 3
        },
        {
          name: "totalAmount",
          label: "Total Amount",
          type: "text",
          disabled: true,
          required: true,
          span: 3
        },
      ]
    },
    {
      heading: "Purchase Details",
      colCnt: 4,
      fieldList: [
        {
          name: "warranty",
          label: "Warranty",
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
          name: "applicablePBGToBeSubmitted",
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
        },
        {
          name: "additionalTermsAndConditions",
          label: "Additional Terms And Conditions(ATC)",
          type: "text",
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
          accept: ".jpg,.jpeg,.png,.gif,.webp,.pdf,.xlsx,.xls,.doc,.docx",
          span:2,
        },
        {
          name: "gemContractFileName",
          label: "Gem Contract Upload",
          type: "multiImage",
          accept: ".jpg,.jpeg,.png,.gif,.webp,.pdf,.xlsx,.xls,.doc,.docx",
          span:2,
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
          options: typeOfSecurityOptions,
        },
        {
          name: "securityNumber",
          label: "Security Number",
          type: "text",
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
          required: true,
          options: [],
        },
        {
          name: "vendorId",
          label: "Vendor Code",
          type: "select",
          required: true,
        },
        {
          name: "vendorAddress",
          label: "Vendor Address",
          type: "text",
          required: true,
          disabled: true,
        },
        {
          name: "vendorsAccountNo",
          label: "Vendor A/C No.",
          type: "text",
          required: true,
          disabled: true,
        },
        {
          name: "vendorsZRSCCode",
          label: "Vendor IFSC Code",
          type: "text",
          required: true,
          disabled: true,
        },
        {
          name: "vendorSwiftCode",
          label: "Vendor SWIFT Code",
          type: "text",
          required: true,
          disabled: true,
        },
        {
          name: "vendorsAccountName",
          label: "Vendor A/C Name",
          type: "text",
          required: true,
          disabled: true,
        }
      ]
    }
  ];
