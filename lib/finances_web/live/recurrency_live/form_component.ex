defmodule FinancesWeb.RecurrencyLive.FormComponent do
  use FinancesWeb, :live_component

  alias Finances.Entries

  @impl true
  def update(%{recurrency: recurrency} = assigns, socket) do
    changeset = Entries.change_recurrency(recurrency)

    {:ok,
     socket
     |> assign(assigns)
     |> assign(:changeset, changeset)}
  end

  @impl true
  def handle_event("validate", %{"recurrency" => recurrency_params}, socket) do
    changeset =
      socket.assigns.recurrency
      |> Entries.change_recurrency(recurrency_params)
      |> Map.put(:action, :validate)

    {:noreply, assign(socket, :changeset, changeset)}
  end

  def handle_event("save", %{"recurrency" => recurrency_params}, socket) do
    save_recurrency(socket, socket.assigns.action, recurrency_params)
  end

  defp save_recurrency(socket, :edit, recurrency_params) do
    case Entries.update_recurrency(socket.assigns.recurrency, recurrency_params) do
      {:ok, _recurrency} ->
        {:noreply,
         socket
         |> put_flash(:info, "Recurrency updated successfully")
         |> push_redirect(to: socket.assigns.return_to)}

      {:error, %Ecto.Changeset{} = changeset} ->
        {:noreply, assign(socket, :changeset, changeset)}
    end
  end

  defp save_recurrency(socket, :new, recurrency_params) do
    case Entries.create_recurrency(recurrency_params) do
      {:ok, _recurrency} ->
        {:noreply,
         socket
         |> put_flash(:info, "Recurrency created successfully")
         |> push_redirect(to: socket.assigns.return_to)}

      {:error, %Ecto.Changeset{} = changeset} ->
        {:noreply, assign(socket, changeset: changeset)}
    end
  end
end
