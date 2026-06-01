import { Form } from 'antd';
import React from 'react';

const FormBody = React.forwardRef((props, ref) => {
  const { children, formData, onFinish } = props; // Destructure props

  const [form] = Form.useForm();

  const updateField = (updatedFormData) => {
    form.setFieldsValue(updatedFormData);
  };

  React.useImperativeHandle(ref, () => ({
    updateField: updateField,
  }));


  return (
    <>
      <Form
        form={form}
        layout="vertical"
        initialValues={formData}
        onFinish={onFinish}
      >
        <div
          className='flex flex-col gap-2 mt-2'
        >
          {children}
        </div>

      </Form>
    </>
  );
});

export default FormBody;
