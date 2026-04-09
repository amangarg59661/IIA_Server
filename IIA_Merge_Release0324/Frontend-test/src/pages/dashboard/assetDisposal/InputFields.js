
export const assetDisposalFields =(locations, isEditable)=> [    
    {
        heading: "Disposal Details",
        colCnt: 4,
        fieldList: [
            {
                name: "disposalDate",
                label: "Disposal Date",
                type: "date",
                span: 2,
                required: true
            },
             {
          name: "locationId",
          label: "Field Station",
          type: "select",
          options: locations,
          required: true,
          span: 2,
        },
        ]
    },
  /*   ...(isEditable
    ? [
        {
          heading: "Disposal Update",
          colCnt: 4,
          fieldList: [
            {
              name: "status",
              label: "Status",
              type: "select",
              options: [
                { value: "Disposed", label: "Disposed" },
                { value: "Removal of Disposal", label: "Removal of Disposal" },
              ],
              span: 2,
              required: true,
            },
            {
              name: "auctionId",
              label: "Auction ID",
              type: "text",
              span: 2,
             
            },
            {
              name: "auctionDate",
              label: "Auction Date",
              type: "date",
              span: 2,
             
            },
            {
              name: "reservePrice",
              label: "Reserve Price",
              type: "text",
              span: 2,
              
            },
            {
              name: "auctionPrice",
              label: "Auction Price",
              type: "text",
              span: 2,
            
            },
            {
              name: "vendorName",
              label: "Vendor Name",
              type: "text",
              span: 2,
             
            },
          ],
        },
      ]
    : []), */
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
                name: "assetCode",
                label: "Asset Code",
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
                name: "quantity",
                label: "Disposal Quantity",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "serialNo",
                label: "Serial No",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "modelNo",
                label: "Model No",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "bookValue",
                label: "Book Value",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "poId",
                label: "Po Id",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "poValue",
                label: "Po Value",
                type: "text",
                span: 1,
                required: true
            },
             {
                name: "poDate",
                label: "Po Delivery Date",
                type: "date",
                span: 1,
                required: true
            },
            {
                name: "disposalCategory",
                label: "Disposal Category",
                type: "select",
                options: [
                    { value: "SCRAP", label: "Scrap" },
                    { value: "SALE", label: "Sale" },
                    { value: "DONATION", label: "Donation" }
                ],
                span: 1,
                required: true
            },
            {
                name: "disposalMode",
                label: "Disposal Mode",
                type: "select",
                options: [
                    { value: "AUCTION", label: "Auction" },
                    { value: "DIRECT_SALE", label: "Direct Sale" },
                    { value: "TENDER", label: "Tender" }
                ],
                span: 1,
                required: true
            },
            {
                name: "locatorId",
                label: "Locator ID",
                type: "text",
                disabled: true,
                span: 2,
                required: true
            },
             {
                name: "reasonForDisposal",
                label: "Reason For Disposal",
                type: "text",
                span: 2,
                required: true
            }
        ]
    }
];