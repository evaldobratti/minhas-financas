defmodule FinancesWeb.RecurrencyLive.Index do
  use FinancesWeb, :live_view

  alias Finances.Entries
  alias Finances.Entries.Recurrency

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :recurrencies, list_recurrencies())}
  end

  @impl true
  def handle_params(params, _url, socket) do
    {:noreply, apply_action(socket, socket.assigns.live_action, params)}
  end

  defp apply_action(socket, :edit, %{"id" => id}) do
    socket
    |> assign(:page_title, "Edit Recurrency")
    |> assign(:recurrency, Entries.get_recurrency!(id))
  end

  defp apply_action(socket, :new, _params) do
    socket
    |> assign(:page_title, "New Recurrency")
    |> assign(:recurrency, %Recurrency{})
  end

  defp apply_action(socket, :index, _params) do
    socket
    |> assign(:page_title, "Listing Recurrencies")
    |> assign(:recurrency, nil)
  end

  @impl true
  def handle_event("delete", %{"id" => id}, socket) do
    recurrency = Entries.get_recurrency!(id)
    {:ok, _} = Entries.delete_recurrency(recurrency)

    {:noreply, assign(socket, :recurrencies, list_recurrencies())}
  end

  defp list_recurrencies do
    Entries.list_recurrencies()
  end
end
