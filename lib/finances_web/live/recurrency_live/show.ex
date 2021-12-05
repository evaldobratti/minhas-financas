defmodule FinancesWeb.RecurrencyLive.Show do
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
     |> assign(:recurrency, Entries.get_recurrency!(id))}
  end

  defp page_title(:show), do: "Show Recurrency"
  defp page_title(:edit), do: "Edit Recurrency"
end
