
 export const grvFields =  [
    {
        heading: "GRV Details",
        colCnt: 4,
        fieldList: [
            {
                name: "giNo",
                label: "GI No",
                type: "search",
                span: 2,
                required: true
            },
            {
                name: "grvNo",
                label: "GRV No",
                type: "search",
              //  disabled: true,
                span: 2,
                //disabled: !!formData?.giNo, 
                // required: true
            },
            {
                name: "date",
                label: "Date",
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
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 2,
                disabled: true,
                required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                disabled: true,
                required: true
            },
            {
                name: "rejectedQuantity",
                label: "Rejected Quantity",
                type: "text",
                span: 1,
                disabled: true,
                required: true
            },
            {
                name: "returnQuantity",
                label: "Return Quantity",
                type: "text",
                span: 3,
                // disabled: true,
                required: true
            },
            {
                name: "returnType",
                label: "Type of Return",
                type: "select",
                options: [
                    { value: "Damaged", label: "Damaged" },
                    { value: "Excess", label: "Excess" },
                    { value: "Wrong Item", label: "Wrong Item" },
                    { value: "Quality Issue", label: "Quality Issue" },
                    { value: "Other", label: "Other" }
                ],
                span: 1,
                required: true
            },
            {
                name: "rejectReason",
                label: "Reason for Return",
                type: "text",
                disabled:true,
                span: 2,
                required: true
            }
        ]
    }
];
 