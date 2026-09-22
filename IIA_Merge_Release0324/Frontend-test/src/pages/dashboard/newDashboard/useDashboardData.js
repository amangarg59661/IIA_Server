import { useState, useEffect, useCallback } from "react";
import axios from "axios";

/**
 * Generic fetch hook shared by all role dashboards.
 *
 * Keeps loading / error / empty handling consistent everywhere instead of
 * each dashboard re-implementing its own axios + useState/useEffect
 * boilerplate (the pattern DashboardOverview.jsx used to do 3-4x per file).
 * A failed call sets `error` and clears `data` — it never throws into the
 * component tree, so one bad widget can't take the rest of the page down.
 *
 * @param {string} url      API endpoint to call (relative, matches existing axios.get usage)
 * @param {object} params   query params object, forwarded to axios
 * @param {any[]}  deps     extra dependencies that should trigger a refetch
 * @param {boolean} enabled set false to skip the call entirely (e.g. while roleName/userId aren't loaded yet)
 */
export default function useDashboardData(url, params = {}, deps = [], enabled = true) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const refetch = useCallback(() => {
    if (!enabled || !url) return;
    setLoading(true);
    setError(null);
    axios
      .get(url, { params })
      .then((res) => {
        setData(res?.data?.responseData ?? null);
      })
      .catch((err) => {
        console.error(`Dashboard fetch failed: ${url}`, err);
        setError(err);
        setData(null);
      })
      .finally(() => setLoading(false));
    // params is intentionally not in the dep array (it's a fresh object each
    // render) — callers pass the relevant primitives via `deps` instead.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [url, enabled, ...deps]);

  useEffect(() => {
    refetch();
  }, [refetch]);

  return { data, loading, error, refetch };
}
