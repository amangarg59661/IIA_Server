import PendingRejectedGiDropdown from "../../../components/PendingRejectedGiDropdown";
export const ogpFields = [
    {
        // heading: "OGP Details",
        colCnt: 4,
        fieldList: [
            {
                name: "ogpType",
                label: "OGP Type",
                type: "select",
                required: true,
                // span: 2,
                options: [
                    {
                        value: "Returnable",
                        label: "Returnable"
                    },
                    {
                        value: "Non Returnable",
                        label: "Non Returnable"
                    }
                ],
            },
            {
                name: "issueNoteId",
                label: "Process No",
                type: "search",
                // span: 2,
                required: true
            },
            {
                name: "ogpId",
                label: "OGP No",
                type: "text",
                disabled: true,
                // span: 2,
                // required: true
            },
            {
                name: "ogpDate",
                label: "OGP Date",
                type: "date",
                required: true
            }
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 4,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 1,
                disabled: true,
                required: true
            },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 2,
                disabled: true,
                required: true
            },
            {
                name: "locatorDesc",
                label: "Locator",
                type: "text",
                span: 2,
                disabled: true,
                required: true
            },
            {
                name: "quantity",
                label: "Quantity",
                type: "text",
                span: 1,
                required: true
            },
        ]
    },
    {
        heading: "Receiver and Sender Details",
        fieldList: [
            {
                name: "senderName",
                label: "Sender Name",
                type: "text",
                required: true,
               // disabled: true
            },
            {
                name: "receiverName",
                label: "Receiver Name",
                type: "text",
                required: true,
            },
            {
                name: "receiverLocation",
                label: "Receiver Location",
                type: "text",
                required: true,
            },
            {
                name: "dateOfReturn",
                label: "Return Date",
                type: "date",
                // required: true,
            }
        ]
    }
];

export const ogpFieldsGiRejected = [
    {
        // heading: "OGP Details",
        colCnt: 4,
        fieldList: [
            {
                name: "ogpType",
                label: "OGP Type",
                type: "select",
                required: true,
                // span: 2,
                options: [
                    {
                        value: "Returnable",
                        label: "Returnable"
                    },
                    {
                        value: "Non Returnable",
                        label: "Non Returnable"
                    }
                ],
            },
          /*  {
                name: "issueNoteId",
                label: "Process No",
                type: "search",
                // span: 2,
                required: true
            },*/{
                  name: "issueNoteId",
                  label: "Process No",
                  type: "customDropdown", 
                  required: true,
                  component: PendingRejectedGiDropdown
                },
            {
                name: "ogpId",
                label: "OGP No",
                type: "text",
                disabled: true,
                // span: 2,
                // required: true
            },
            {
                name: "ogpDate",
                label: "OGP Date",
                type: "date",
                required: true
            }
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 4,
        children: [
            {
                name: "assetId",
                label: "Asset ID",
                type: "text",
                span: 1,
                disabled: true,
                // required: true
            },
             {
          name: "assetCode",
          label: "Asset Code",
          type: "text",
          span: 1,
          disabled: true,
          // required: true,
        },
            {
                name: "assetDesc",
                label: "Asset Description",
                type: "text",
                span: 1,
                disabled: true,
                // required: true
            },
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 1,
                disabled: true,
                // required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 1,
                disabled: true,
                // required: true
            },
            // {
            //     name: "locatorDesc",
            //     label: "Locator",
            //     type: "text",
            //     span: 2,
            //     disabled: true,
            //     required: true
            // },
            {
                name: "rejectedQuantity",
                label: "Rejected Quantity",
                type: "text",
                span: 1,
                required: true
            },
            {
                name: "rejectionType",
                label: "Rejection Type",
                type: "text",
                span: 1,
                required: true
            },
        ]
    },
    {
        heading: "Receiver and Sender Details",
        fieldList: [
            {
                name: "senderName",
                label: "Sender Name",
                type: "text",
                required: true,
               // disabled: true
            },
            {
                name: "receiverName",
                label: "Receiver Name",
                type: "text",
                required: true,
            },
            {
                name: "receiverLocation",
                label: "Receiver Location",
                type: "text",
                required: true,
            },
            {
                name: "dateOfReturn",
                label: "Return Date",
                type: "date",
                // required: true,
            }
        ]
    }
];

export const ogpFieldsPo = [
    {
        // heading: "OGP Details",
        colCnt: 4,
        fieldList: [
            {
                name: "ogpType",
                label: "OGP Type",
                type: "select",
                required: true,
                // span: 2,
                options: [
                    {
                        value: "Returnable",
                        label: "Returnable"
                    },
                    {
                        value: "Non Returnable",
                        label: "Non Returnable"
                    }
                ],
            },
            {
                name: "issueNoteId",
                label: "Process No",
                type: "search",
                // span: 2,
                required: true
            },
            {
                name: "ogpId",
                label: "OGP No",
                type: "text",
                disabled: true,
                // span: 2,
                // required: true
            },
            {
                name: "ogpDate",
                label: "OGP Date",
                type: "date",
                required: true
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
                span: 1,
                disabled: true,
                required: true
            },
            {
                name: "materialDescription",
                label: "Material Description",
                type: "text",
                span: 2,
                disabled: true,
                required: true
            },
            // {
            //     name: "locatorDesc",
            //     label: "Locator",
            //     type: "text",
            //     span: 2,
            //     disabled: true,
            //     required: true
            // },
            {
                name: "quantity",
                label: "Quantity",
                type: "text",
                span: 1,
                required: true
            },
            {
                name: "uom",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },
        ]
    },
    {
        heading: "Receiver And Sender Details",
        fieldList: [
             {
                 name: "senderName",
                 label: "Sender Name",
                 type: "text",
                 required: true,
                // disabled: true
             },
            {
                name: "receiverName",
                label: "Receiver Name",
                type: "text",
                required: true,
            },
            {
                name: "receiverLocation",
                label: "Receiver Location",
                type: "text",
                required: true,
            },
            {
                name: "dateOfReturn",
                label: "Return Date",
                type: "date",
                // required: true,
            }
        ]
    }
];


export const gtOgpFields = [
    {
      fieldList: [
        {
          name: "gtId",
          label: "Goods Transfer ID",
          type: "search",
          // required: true,
        //   span: 2,
        },
        {
          name: "ogpId",
          label: "OGP ID",
          type: "text",
          // required: true,
        //   span: 1,
        },
      ]
    },
    {
      heading: "Transfer Information",
      colCnt: 4,
      fieldList: [
        {
          name: "senderLocationIdDesc",
          label: "Sender Field Station",
          type: "text",
        //   options: formattedLocations,
          required: true,
          span: 2,
        },
        {
          name: "senderCustodianIdDesc",
          label: "Sender Custodian",
          type: "text",
        //   options: indentList,
          required: true,
          span: 2,
        },
        {
          name: "receiverLocationIdDesc",
          label: "Receiver Field Station",
          type: "text",
        //   options: formattedLocations,
          required: true,
          span: 2,
        },
        {
          name: "receiverCustodianIdDesc",
          label: "Receiver Custodian",
          type: "text",
        //   options: indentList,
          required: true,
          span: 2,
        },
        {
          name: "gtDate",
          label: "Goods Transfer Date",
          type: "date",
          required: true,
          span: 1,
        },
        {
          name: "ogpDate",
          label: "OGP Date",
          type: "date",
          required: true,
          span: 1,
        },
      ],
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
          span: 2,
          // required: true,
        },
        {
          name: "materialDesc",
          label: "Material Description",
          type: "text",
          span: 3,
          // required: true,
        },
        {
          name: "assetCode",
          label: "Asset Code",
          type: "text",
          span: 2,
          // required: true,
        },
        {
          name: "assetId",
          label: "Asset Id",
          type: "text",
          span: 2,
          // required: true,
        },
        {
          name: "assetDesc",
          label: "Asset Description",
          type: "text",
          span: 3,
          // required: true,
        },
        // {
        //   name: "uomId",
        //   label: "UOM",
        //   type: "text",
        //   span: 1,
        //   required: true,
        // },
        {
          name: "receiverLocatorIdDesc",
          label: "Receiver Locator",
          type: "text",
        //   options: ldd || [],
          span: 2,
          required: true,
        },
        {
          name: "senderLocatorIdDesc",
          label: "Sender Locator",
          type: "text",
          span: 2,
          required: true,
        },
        {
          name: "quantity",
          label: "Quantity",
          type: "text",
          span: 2,
          required: true,
        },
                {
          name: "unitPrice",
          label: "Unit Price",
          type: "text",
          span: 2,
          required: true
        },
        {
          name: "depriciationRate",
          label: "Depriciation Rate",
          type: "text",
          span: 2,
          required: true
        },
          {
          name: "bookValue",
          label: "Book Value",
          type: "text",
          span: 2,
          required: true
        } ,{
          name: "serialNo",
          label: "Serial No",
          type: "text",
          span: 2,
          required: true
        }

      ],
    },
  ];
  export const assetDisposalFields = [
  {
    heading: "Auction Details",
    colCnt: 4,
    fieldList: [
      {
        name: "auctionId",
        label: "Auction ID",
        type: "search",
        disabled: true,
      },
      {
        name: "auctionCode",
        label: "Auction Code",
        type: "text",
        disabled: true,
      },
      {
        name: "auctionDate",
        label: "Auction Date",
        type: "date",
        required: true,
      },
      {
        name: "vendorName",
        label: "Vendor Name",
        type: "text",
      },
      {
        name: "reservePrice",
        label: "Reserve Price",
        type: "text",
      },
      {
        name: "auctionPrice",
        label: "Auction Price",
        type: "text",
      },
    ],
  },
  {
    heading: "Material Details",
    name: "assets", 
    colCnt: 4,
    children: [
      {
        name: "disposalId",
        label: "Disposal ID",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "assetId",
        label: "Asset ID",
        type: "text",
        span: 1,
        disabled: true,
      }, {
        name: "assetCode",
        label: "Asset Code",
        type: "text",
        span: 2,
        disabled: true,
      },
      {
        name: "assetDesc",
        label: "Asset Description",
        type: "text",
        span: 2,
        disabled: true,
      },
      {
        name: "locatorId",
        label: "Locator",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "custodianId",
        label: "Custodian ID",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "disposalQuantity", 
        label: "Quantity",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "serialNo", 
        label: "Serial No",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "unitPrice",
        label: "Unit Price",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "bookValue",
        label: "Book Value",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "reasonForDisposal",
        label: "Reason for Disposal",
        type: "text",
        span: 2,
        disabled: true,
      },
    /*  {
        name: "disposalDate",
        label: "Disposal Date",
        type: "date",
        span: 1,
        disabled: true,
      },*/
      {
        name: "locationId",
        label: "Location",
        type: "text",
        span: 1,
        disabled: true,
      },
      {
        name: "status",
        label: "Status",
        type: "text",
        span: 1,
        disabled: true,
      },
    ],
  },


  
];
