// {
//     heading: "Goods Installation Details",
//     colCnt: 4,
//     fieldList: [
//         {
//             name: "goodsInpectionNo", // required
//             label: "Goods Inpection No", // optional
//             type:"text", // required
//             disabled: true, //optional
//             required: true // option
//         },

export const generalDtls = [
    {
        heading: "Order Details", // optional
        colCnt: 5, // optional
        fieldList: [
            {
                name: "gprnNo", // required
                label: "GPRN No", // optional
                type:"search", // required
                disabled: true, //optional
                required: true, // option
                span: 2
            },
            {
                name: "giNo",
                label: "Gi No.",
                type: "text",
                disabled: true,
                span: 2
                // required: true
            },
            {
                name: "date",
                label: "Date",
                type: "date",
                required: true
            },
            {
                name: "installationDate",
                label: "Installation Date",
                type: "date",
                required: true
            },
            {
                name: "commissioningDate",
                label: "Commission Date",
                type: "date",
                required: true
            },
            // {
            //     name: "project",
            //     label: "Project",
            //     type: "text",
            //     required: true,
            //     span: 2 // optional
            // }

        ]
    },
    {
        heading: "Vendor Details",
        colCnt: 10,
        fieldList: [
            {
                name: "vendorId",
                label: "Vendor ID",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "vendorName",
                label: "Vendor Name",
                type: "text",
                span: 3,
                required: true
            },
            {
                name: "vendorEmail",
                label: "Vendor Email",
                type: "text",
                span: 3,
                required: true
            },
            {
                name: "vendorContact",
                label: "Vendor Contact",
                type: "text",
                span: 2,
                required: true
            }
        ]
    },
    {
        heading: "Delivery & Invoice Details",
        colCnt: 5,
        fieldList: [
            {
                name: "challanNo",
                label: "Challan/Invoice No.",
                type: "text",
                required: true,
                span: 2
            },
            {
                name: "deliveryDate",
                label: "Delivery Date",
                type: "date",
                required: true,
                disabled: true,
                span: 1
            },
            {
                name: "supplyExpectedDate",
                label: "Date of Supply",
                type: "date",
                required: true,
                span: 1
            },
            {
                name: "fieldStation",
                label: "Field Station",
                type: "text",
                required: true,
                span: 2
            },
            {
                name: "indentorName",
                label: "Indentor Name",
                type: "text",
                required: true,
                span: 2
            },
        ]
    },
    {
        heading: "Material Details",
        name: "materialDtlList",
        colCnt: 8,
        children: [
            {
                name: "materialCode",
                label: "Material Code",
                type: "text",
                span: 2,
                required: true
            },
            {
                name: "materialDesc",
                label: "Material Description",
                type: "text",
                span: 3,
                required: true
            },
            {
                name: "uomId",
                label: "UOM",
                type: "text",
                span: 1,
                required: true
            },

            // {
            //     name: "warranty",
            //     label: "Warranty",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "orderedQuantity",
            //     label: "Ordered Quantity",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "quantityDelivered",
            //     label: "Quantity Delivered",
            //     type: "text",
            //     required: true
            // },
            {
                name: "receivedQuantity",
                label: "Received Quantity",
                type: "text",
                disabled: true,
                required: true
            },
            {
                name: "acceptedQuantity",
                label: "Accepted Quantity",
                type: "text",
                required: true
            },
            {
                name: "rejectedQuantity",
                label: "Rejected Quantity",
                type: "text",
                disabled: true,
                required: true
            },
            {
                name: "rejectionReason",
                label: "Reason for Rejection",
                type: "text",
                span: 3,
                required: true,
                disabled: (formData, index) => {
                    const materialList = formData?.materialDtlList || [];
                    return !materialList[index]?.rejectedQuantity || materialList[index]?.rejectedQuantity <= 0;
                }
            },
            {
                name: "installationRepostBase64",
                label: "Installation Report",
                type: "image",
                span: 3,
              //  required: true,
                accept: "image/*"
            },
            // {
            //     name: "unitPrice",
            //     label: "Unit Price",
            //     type: "text",
            //     required: true
            // },
            // {
            //     name: "makeNo",
            //     label: "Make No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "modelNo",
            //     label: "Model No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "serialNo",
            //     label: "Serial No.",
            //     type: "text",
            //     span: 2,
            //     required: true
            // },
            // {
            //     name: "note",
            //     label: "Note",
            //     type: "text",
            //     span: 5,
            //     required: true
            // },
            // {
            //     name: "photographPath",
            //     label: "Photograph",
            //     type: "text",
            //     required: true
            // }
        ]
    },
    {
        heading: "Consignee & Warranty Information",
        colCnt: 3,
        fieldList: [
            {
                name: "consigneeDetail",
                label: "Consignee Details",
                type: "text",
                required: true,
                span: 2
            },
            // {
            //     name: "warrantyYears",
            //     label: "Warranty Years",
            //     type: "text",
            //     required: true
            // }
        ]
    },
    // {
    //     heading: "Quantity & Acceptance Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "receivedQty",
    //             label: "Received Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "pendingQty",
    //             label: "Pending Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "acceptedQty",
    //             label: "Accepted Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "receivedBy",
    //             label: "Received By",
    //             type: "text",
    //             required: true
    //         }
    //     ]
    // },
    // {
    //     heading: "Goods Installation Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "goodsInpectionNo", // required
    //             label: "Goods Inpection No", // optional
    //             type:"text", // required
    //             disabled: true, //optional
    //             required: true // option
    //         },
    //         {
    //             name: "installationDate",
    //             label: "Installation Date",
    //             type: "date",
    //             required: true
    //         },
    //         {
    //             name: "commissioningDate",
    //             label: "Commissioning Date",
    //             type: "date",
    //             required: true
    //         },
            // {
            //     name: "uploadInstallationReport",
            //     label: "Upload Installation Report",
            //     type: "text",
            // }
        // ]
    // },
    // {
    //     heading: "Quantity Details",
    //     colCnt: 4,
    //     fieldList: [
    //         {
    //             name: "acceptedQuantity",
    //             label: "Accepted Quantity",
    //             type: "text",
    //             required: true
    //         },
    //         {
    //             name: "rejectedQuantity",
    //             label: "Rejected Quantity",
    //             type: "text",
    //             required: true
    //         }
    //     ]
    // },
    // {
    //     heading: "Return Details",
    //     colCnt: 4,
    //     fieldList:[
    //         {
    //             name: "goodsReturn",
    //             label: "Goods Return",
    //             type: "text",
    //             required: true,
    //             span: 2
    //         }
    //     ]
    // }
]

