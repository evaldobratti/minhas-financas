defmodule FinancesWeb.HomeLive.Index do
  use FinancesWeb, :live_view

  alias Finances.Entries
  alias Finances.Entries.Account
  alias Finances.Entries.Entry

  @impl true
  def mount(_params, _session, socket) do
    {
      :ok,
      socket
      |> assign(accounts: Entries.list_accounts())
      |> assign(account: nil)
    }
  end
  
  @impl true
  def handle_params(params, _url, socket) do
    {:noreply, apply_action(socket, socket.assigns.live_action, params)}
  end

  def apply_action(socket, :index, _params) do
    socket
  end

  def apply_action(socket, :account_new, _params) do
    socket
    |> assign(account: %Account{})
    |> assign(page_title: "New account")
  end

  def apply_action(socket, :account_edit, %{"id" => id}) do
    socket
    |> assign(account: Entries.get_account!(id))
    |> assign(page_title: "Edit account")
  end

  def apply_action(socket, :account_show, %{"id" => id}) do
    account = Entries.get_account!(id)
    entries = Entries.get_entries(account)

    socket
    |> assign(date: Timex.today() |> Timex.beginning_of_month())
    |> assign(account: account)
    |> assign(page_title: account.name)
    |> assign(entries: entries)
  end

  def apply_action(socket, :entry_new, params) do
    socket
    |> apply_action(:account_show, params)
    |> assign(entry: %Entry{})
    |> assign(page_title: "New entry")
  end

  @impl true
  def handle_event("month-previous", _params, socket) do
    date = Timex.shift(socket.assigns.date, months: -1)

    {
      :noreply, 
      socket
      |> assign(date: date)
    }
  end

  @impl true
  def handle_event("month-next", _params, socket) do
    date = Timex.shift(socket.assigns.date, months: 1)

    {
      :noreply, 
      socket
      |> assign(date: date)
    }
  end

end

