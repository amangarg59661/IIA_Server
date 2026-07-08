import React, { useEffect, useState } from 'react'
import {Form, Input, InputNumber} from "antd"
import { EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons'

const FormInputItem = ({label, name, value, onChange, readOnly, disabled, className, placeholder, required, min, max, type = 'text'}) => {
  const [showPassword, setShowPassword] = useState(false)

  useEffect(() => {
    if (type !== 'password') {
      setShowPassword(false)
    }
  }, [type])

  const handleChange = (e) => {
    if(onChange)
      if(typeof name === 'string')
        onChange(name, e.target.value)
      else
        onChange(name[2], e.target.value)
  }

  const inputType = type === 'password' ? (showPassword ? 'text' : 'password') : type
  const suffix = type === 'password' ? (
    <span
      onClick={() => setShowPassword((prev) => !prev)}
      style={{ cursor: 'pointer', color: 'rgba(0,0,0,0.45)' }}
      title={showPassword ? 'Hide password' : 'Show password'}
    >
      {showPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />}
    </span>
  ) : undefined

  return (
    <Form.Item label={label} name={name}
      // rules = {rules}
      rules={[{ required: required ? true : false, message: 'Please input a value!' }]}
      // className={`!mb-4 flex flex-col sm:flex-row items-center ${className}`}
      className={className}
    >
      {
        type === 'number' ? 
        <InputNumber
          step={1}          // Increment/decrement step
          value={value}
          min={min}
          max={max}
          onChange={handleChange}
          style={{ width: '100%' }} // Adjust width as needed
        />
        :
        <Input
          type={inputType}
          value={value}
          onChange={handleChange}
          readOnly={readOnly}
          disabled={disabled}
          placeholder={placeholder}
          suffix={suffix}
        />
      }
    </Form.Item>
  )
}

export default FormInputItem