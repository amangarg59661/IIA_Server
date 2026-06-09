import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate, Outlet } from 'react-router-dom'

const PrivateRoutes = () => {
  const { token } = useSelector(state => state.auth)

  if (!token) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}

export default PrivateRoutes;