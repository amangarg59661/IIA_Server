export const SoDetails = [
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
        },
        {
            name: "gst",
            label: "GST (%)",
            type: "text",
            required: true
        },
        {
            name: "duties",
            label: "Duties (%)",
            type: "text",
            required: true
        },
        {
          name: "totalAmount",
          label: "Total Amount",  type: "text",
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
          type: "text",
          span: 2
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