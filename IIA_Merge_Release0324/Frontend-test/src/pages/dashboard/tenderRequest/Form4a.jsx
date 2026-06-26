import React, { useState } from "react";
import { Button, Form, Upload } from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useSelector } from "react-redux";
import FormContainer from "../../../components/DKG_FormContainer";
import FormBody from "../../../components/DKG_FormBody";
import Heading from "../../../components/DKG_Heading";
import FormDropdownItem from "../../../components/DKG_FormDropdownItem";
import Btn from "../../../components/DKG_Btn";
import { useLocation } from "react-router-dom";
import FileUpload from "../../../components/DKG_FileUpload";
import TenderEvaluator from "./TenderEvaluator";
import IndentorUpload from "./IndentorUpload";

const Form4a = () => {
  const [form] = Form.useForm();
  const onFinish = (values) => {
    ;
  };

  const {roleId} = useSelector(state => state.auth)


  // // Access the logged-in user's role from the Redux store
  // const userRole = useSelector((state) => state.auth);
  // const role = userRole.role;

  const location = useLocation();
  const tenderId = location.state?.tenderId || null;
  const bidType = location.state?.bidType || null;
  const requestId =  location.state?.requestId ||null;
  const parsedRoleId = parseInt(roleId);
    // tender evaluator
   /* if(parseInt(roleId) === 17 ){
      return <TenderEvaluator bidType={bidType} tenderId={tenderId} />
    }


    if(parseInt(roleId) === 1){
      return <IndentorUpload requestId={requestId} />
    }*/
    if (parsedRoleId === 1 && requestId ) {
      return <IndentorUpload requestId={requestId} />
    }

    if (parsedRoleId === 17 || (parsedRoleId === 1 && tenderId && bidType)) {
      return <TenderEvaluator bidType={bidType} tenderId={tenderId} />
    }


  return (
    <FormContainer>
      <FormBody ref={form} onFinish={onFinish}>
        <Heading title={`Tender Evaluation for Tender ID: ${tenderId} and Bid Type: ${bidType}`} />

        <div className="custom-btn">
          <Btn>Approve</Btn>
        </div>
      </FormBody>
    </FormContainer>
  );
};

export default Form4a;




// <div className="form-section">
// {/* Tender ID is visible to everyone */}
// {/* <FormDropdownItem
//   label="Tender ID"
//   formField="tenderId"
//   placeholder="Select Tender ID"
//   name="tenderId"
//   onChange={form.updateField}
//   dropdownArray={[]} // Fetch from API
//   required
// /> */}

// {/* This field is visible only to directors */}
// {role === "Director" && (
//   <FormDropdownItem
//     label="Formation of techno commercial committee"
//     formField="committeeFormation"
//     placeholder="Select Formation of techno commercial committee"
//     name="committeeFormation"
//     onChange={form.updateField}
//     dropdownArray={[]} // Fetch from API
//     required
//   />
// )}
// </div>

// {/* Fields visible only to purchase department */}
// {role === "Purchase Dept" && (
// <>
//   {/* Single bid vendor upload */}
//   <Form.Item
//     name="vendorUploadSingle"
//     label="Vendor Upload - Single Bid"
//     rules={[
//       { required: true, message: "Vendor documents are required" },
//     ]}
//   >
//     <Upload beforeUpload={() => false}>
//       <Button icon={<UploadOutlined />}>
//         Upload Single Bid Vendor Documents
//       </Button>
//     </Upload>
//   </Form.Item>

//   {/* Double bid vendor upload */}
//   <Form.Item
//     name="vendorUploadDouble"
//     label="Vendor Upload - Double Bid"
//     rules={[
//       { required: true, message: "Vendor documents are required" },
//     ]}
//   >
//     <Upload beforeUpload={() => false}>
//       <Button icon={<UploadOutlined />}>
//         Upload Double Bid Vendor Documents
//       </Button>
//     </Upload>
//   </Form.Item>
// </>
// )}

// {role === "Indent Creator" && (
//   <>    
//   <Form.Item
//   name="commerciallyQualifiedVendors"
//   label="Commercially Qualified Vendors"
//   rules={[
//       { required: true, message: "Vendor documents are required" },
//   ]}
//   >
//   <Upload beforeUpload={() => false}>
//       <Button icon={<UploadOutlined />}>
//       Upload Commercially Qualified Vendors
//       </Button>
//   </Upload>
//   </Form.Item>
//   <Form.Item
//   name="technicallyQualifiedVendors"
//   label="Technically Qualified Vendors"
//   rules={[
//       { required: true, message: "Vendor documents are required" },
//   ]}
//   >
//   <Upload beforeUpload={() => false}>
//       <Button icon={<UploadOutlined />}>
//       Upload Technically Qualified Vendors
//       </Button>
//   </Upload>
//   </Form.Item>
//   </>
// )}
