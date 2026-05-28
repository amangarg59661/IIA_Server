import { Button } from 'antd'
import React from 'react'

const Btn = ({text, children, onClick, htmlType, className, loading}) => {
  return (
    <Button loading={loading} htmlType={htmlType} onClick={onClick} type='primary' className={` text-right bg-darkBlue hover:!bg-darkBlueHover w-fit text-offWhite uppercase ${className}`}>
      {text ? text : children}
    </Button>
  )
}

export default Btn