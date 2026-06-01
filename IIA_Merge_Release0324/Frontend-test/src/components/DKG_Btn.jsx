import { Button } from 'antd'
import React from 'react'

const Btn = ({text, children, onClick, htmlType, className, loading, type}) => {
  return (
    <Button loading={loading} htmlType={htmlType} onClick={onClick} className={` text-right bg-darkBlue hover:!bg-darkBlueHover w-fit text-offWhite uppercase ${className}`}>
      {text ? text : children}
    </Button>
  )
}

export default Btn
