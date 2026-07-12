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
      getValueProps={(value) => ({
        value: value ? (typeof value === "string" ? dayjs(value, dateFormat) : value) : null,
      })}
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