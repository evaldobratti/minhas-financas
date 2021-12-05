defmodule FinancesWeb.TransferLive.Index do
  use FinancesWeb, :live_view

  alias Finances.Entries
  alias Finances.Entries.Transfer

  @impl true
  def mount(_params, _session, socket) do
    {:ok, assign(socket, :transfers, list_transfers())}
  end

  @impl true
  def handle_params(params, _url, socket) do
    {:noreply, apply_action(socket, socket.assigns.live_action, params)}
  end

  defp apply_action(socket, :edit, %{"id" => id}) do
    socket
    |> assign(:page_title, "Edit Transfer")
    |> assign(:transfer, Entries.get_transfer!(id))
  end

  defp apply_action(socket, :new, _params) do
    socket
    |> assign(:page_title, "New Transfer")
    |> assign(:transfer, %Transfer{})
  end

  defp apply_action(socket, :index, _params) do
    socket
    |> assign(:page_title, "Listing Transfers")
    |> assign(:transfer, nil)
  end

  @impl true
  def handle_event("delete", %{"id" => id}, socket) do
    transfer = Entries.get_transfer!(id)
    {:ok, _} = Entries.delete_transfer(transfer)

    {:noreply, assign(socket, :transfers, list_transfers())}
  end

  defp list_transfers do
    Entries.list_transfers()
  end
end
