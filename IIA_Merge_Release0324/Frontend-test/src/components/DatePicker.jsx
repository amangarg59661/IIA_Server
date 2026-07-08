// import React from 'react';
// import { DatePicker, Form } from 'antd';
// import dayjs from 'dayjs';
// import customParseFormat from 'dayjs/plugin/customParseFormat';

// dayjs.extend(customParseFormat);

// const InputDatePicker = ({ label, name, disabled, onChange, defaultValue, required }) => {
// //   const handleDateChange = (date) => {
// //     if (onChange) {
// //       if (typeof name === 'string') {
// //         onChange(name, date ? date.format('DD/MM/YYYY') : null);
// //       } else if (Array.isArray(name)) {
// //         onChange(name[2], date ? date.format('DD/MM/YYYY') : null);
// //       }
// //     }
// //   };

// const handleDateChange = (date) => {
//     // Ensure the date is a valid Day.js object
//     if (date && dayjs.isDayjs(date)) {
//       if (onChange) {
//         if (typeof name === 'string') {
//           onChange(name, date.format('DD/MM/YYYY'));
//         } else if (Array.isArray(name)) {
//           onChange(name[2], date.format('DD/MM/YYYY'));
//         }
//       }
//     } else {
//       if (onChange) {
//         // If the date is invalid or null, pass null to onChange
//         onChange(name, null);
//       }
//     }
//   };

//   return (
//     <Form.Item
//       label={label}
//       name={name}
//       className="mb-4"
//       rules={[
//         {
//           required: required,
//           message: `Please select ${label}`
//         }
//       ]}
//     >
//       <DatePicker 
//         className="w-full" 
//         disabled={disabled}
//         onChange={handleDateChange}
//         format="DD/MM/YYYY"
//       />
//     </Form.Item>
//   );
// };

// export default InputDatePicker;


// import React from "react";
// import { Form, DatePicker } from "antd";
// import dayjs from "dayjs";
// const dateFormat = "DD/MM/YYYY";

// const FormDatePickerItem = ({
//   label,
//   name,
//   defaultValue,
//   onChange,
//   value,
//   readOnly,
//   required,
// }) => {
//   const currentDate = dayjs();
//   const defVal = defaultValue ? defaultValue : currentDate.format(dateFormat);
//   const handleDateChange = (date, dateString) => {
//     if (dateString === "") {
//       onChange(name, null);
//     } else {
//       onChange(name, dateString);
//     }
//   };

//   return (
//     <Form.Item
//       label={label}
//       rules={[
//         { required: required ? true : false, message: "Please input value!" },
//       ]}
//     >
//       <DatePicker
//         disabled={readOnly}
//         defaultValue={dayjs(defVal, dateFormat, true)}
//         style={{ width: "100%" }}
//         format={dateFormat}
//         // value={value ? dayjs(value, dateFormat, true) : dayjs(defVal, dateFormat, true)}
//         name={name}
//         onChange={handleDateChange}
//       />
//     </Form.Item>
//   );
// };

// export default FormDatePickerItem;


import React from "react";
import { Form, DatePicker } from "antd";
import dayjs from "dayjs";

const dateFormat = "DD/MM/YYYY";

const InputDatePicker = ({
  label,
  name,
  defaultValue,
  onChange,
  readOnly,
  required,
  rules = [],
}) => {
  const initialValue = defaultValue ? dayjs(defaultValue, dateFormat) : null;

  const handleDateChange = (date, dateString) => {
    if (onChange) {
      if (date && dayjs.isDayjs(date)) {
        onChange(name, dateString || date.format(dateFormat));
      } else {
        onChange(name, null);
      }
    }
  };

  return (
    <Form.Item
      name={name}
      label={label}
      rules={
        rules.length
          ? rules
          : required
          ? [{ required: true, message: `Please select ${label}` }]
          : []
      }
      initialValue={initialValue}
    >
      <DatePicker
        style={{ width: "100%" }}
        format={dateFormat}
        disabled={readOnly}
        onChange={handleDateChange}
      />
    </Form.Item>
  );
};

export default InputDatePicker;
