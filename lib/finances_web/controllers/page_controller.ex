defmodule FinancesWeb.PageController do
  use FinancesWeb, :controller

  
  def index(conn, _params) do
    render(conn, "index.html")
  end
end
