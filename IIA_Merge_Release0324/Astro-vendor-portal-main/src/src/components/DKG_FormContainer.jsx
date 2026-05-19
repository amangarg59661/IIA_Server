import React from 'react'

const FormContainer = ({children, className}) => {
  return (
    <div className={`md:w-full h-[100vh] md:h-fit md:border bg-white md:border-gray-400 p-4 md:px-8 md:py-1 rounded-md md:shadow-2xl md:mx-auto overflow-auto flex flex-col gap-4 md:gap-2 ${className}`}>
      {children}
    </div>
  )
}

export default FormContainer