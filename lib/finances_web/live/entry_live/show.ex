defmodule FinancesWeb.EntryLive.Show do
  use FinancesWeb, :live_view

  alias Finances.Entries

  @impl true
  def mount(_params, _session, socket) do
    {:ok, socket}
  end

  @impl true
  def handle_params(%{"id" => id}, _, socket) do
    {:noreply,
     socket
     |> assign(:page_title, page_title(socket.assigns.live_action))
     |> assign(:entry, Entries.get_entry!(id))}
  end

  defp page_title(:show), do: "Show Entry"
  defp page_title(:edit), do: "Edit Entry"
end
