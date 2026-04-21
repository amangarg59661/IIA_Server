import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';

//axios.defaults.baseURL="http://103.181.158.220:8081/astro-service"
//axios.defaults.baseURL="http://localhost:8081/astro-service"
export const baseURL = "http://localhost:8088/astro-service";
axios.defaults.baseURL = baseURL;
//export const baseURL = "/astro-service";
//axios.defaults.baseURL = baseURL;
//export const baseURL = "/astro-service";
//axios.defaults.baseURL = baseURL;

//axios.defaults.baseURL="/astro-service"

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMasters());
  }, [dispatch])

  return (
    <Routes />
  );
}

export default App;