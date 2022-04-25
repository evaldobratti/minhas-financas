defmodule FinancesWeb.Router do
  use FinancesWeb, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_live_flash
    plug :put_root_layout, {FinancesWeb.LayoutView, :root}
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/", FinancesWeb do
    pipe_through :browser

    get "/", PageController, :index

    live "/accounts", AccountLive.Index, :index
    live "/accounts/new", AccountLive.Index, :new
    live "/accounts/:id/edit", AccountLive.Index, :edit

    live "/accounts/:id", AccountLive.Show, :show
    live "/accounts/:id/show/edit", AccountLive.Show, :edit

    live "/entries", EntryLive.Index, :index
    live "/entries/new", EntryLive.Index, :new
    live "/entries/:id/edit", EntryLive.Index, :edit
    live "/entries/:id", EntryLive.Show, :show
    live "/entries/:id/show/edit", EntryLive.Show, :edit

    live "/recurrencies", RecurrencyLive.Index, :index
    live "/recurrencies/new", RecurrencyLive.Index, :new
    live "/recurrencies/:id/edit", RecurrencyLive.Index, :edit
    live "/recurrencies/:id", RecurrencyLive.Show, :show
    live "/recurrencies/:id/show/edit", RecurrencyLive.Show, :edit

    live "/transfers", TransferLive.Index, :index
    live "/transfers/new", TransferLive.Index, :new
    live "/transfers/:id/edit", TransferLive.Index, :edit
    live "/transfers/:id", TransferLive.Show, :show
    live "/transfers/:id/show/edit", TransferLive.Show, :edit
  end

  # Other scopes may use custom stacks.
  # scope "/api", FinancesWeb do
  #   pipe_through :api
  # end

  # Enables LiveDashboard only for development
  #
  # If you want to use the LiveDashboard in production, you should put
  # it behind authentication and allow only admins to access it.
  # If your application does not have an admins-only section yet,
  # you can use Plug.BasicAuth to set up some basic authentication
  # as long as you are also using SSL (which you should anyway).
  if Mix.env() in [:dev, :test] do
    import Phoenix.LiveDashboard.Router

    scope "/" do
      pipe_through :browser
      live_dashboard "/dashboard", metrics: FinancesWeb.Telemetry
    end
  end


  # Enables the Swoosh mailbox preview in development.
  #
  # Note that preview only shows emails that were sent by the same
  # node running the Phoenix server.
  if Mix.env() == :dev do
    scope "/dev" do
      pipe_through :browser

      forward "/mailbox", Plug.Swoosh.MailboxPreview
    end
  end
end
