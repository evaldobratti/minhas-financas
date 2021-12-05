defmodule FinancesWeb.RecurrencyLiveTest do
  use FinancesWeb.ConnCase

  import Phoenix.LiveViewTest
  import Finances.EntriesFixtures

  @create_attrs %{date_end: %{day: 4, month: 12, year: 2021}, date_start: %{day: 4, month: 12, year: 2021}, description: "some description", frequency: "some frequency", is_forever: true, is_parcel: true, parcel_end: 42, parcel_start: 42, value: "120.5"}
  @update_attrs %{date_end: %{day: 5, month: 12, year: 2021}, date_start: %{day: 5, month: 12, year: 2021}, description: "some updated description", frequency: "some updated frequency", is_forever: false, is_parcel: false, parcel_end: 43, parcel_start: 43, value: "456.7"}
  @invalid_attrs %{date_end: %{day: 30, month: 2, year: 2021}, date_start: %{day: 30, month: 2, year: 2021}, description: nil, frequency: nil, is_forever: false, is_parcel: false, parcel_end: nil, parcel_start: nil, value: nil}

  defp create_recurrency(_) do
    recurrency = recurrency_fixture()
    %{recurrency: recurrency}
  end

  describe "Index" do
    setup [:create_recurrency]

    test "lists all recurrencies", %{conn: conn, recurrency: recurrency} do
      {:ok, _index_live, html} = live(conn, Routes.recurrency_index_path(conn, :index))

      assert html =~ "Listing Recurrencies"
      assert html =~ recurrency.description
    end

    test "saves new recurrency", %{conn: conn} do
      {:ok, index_live, _html} = live(conn, Routes.recurrency_index_path(conn, :index))

      assert index_live |> element("a", "New Recurrency") |> render_click() =~
               "New Recurrency"

      assert_patch(index_live, Routes.recurrency_index_path(conn, :new))

      assert index_live
             |> form("#recurrency-form", recurrency: @invalid_attrs)
             |> render_change() =~ "is invalid"

      {:ok, _, html} =
        index_live
        |> form("#recurrency-form", recurrency: @create_attrs)
        |> render_submit()
        |> follow_redirect(conn, Routes.recurrency_index_path(conn, :index))

      assert html =~ "Recurrency created successfully"
      assert html =~ "some description"
    end

    test "updates recurrency in listing", %{conn: conn, recurrency: recurrency} do
      {:ok, index_live, _html} = live(conn, Routes.recurrency_index_path(conn, :index))

      assert index_live |> element("#recurrency-#{recurrency.id} a", "Edit") |> render_click() =~
               "Edit Recurrency"

      assert_patch(index_live, Routes.recurrency_index_path(conn, :edit, recurrency))

      assert index_live
             |> form("#recurrency-form", recurrency: @invalid_attrs)
             |> render_change() =~ "is invalid"

      {:ok, _, html} =
        index_live
        |> form("#recurrency-form", recurrency: @update_attrs)
        |> render_submit()
        |> follow_redirect(conn, Routes.recurrency_index_path(conn, :index))

      assert html =~ "Recurrency updated successfully"
      assert html =~ "some updated description"
    end

    test "deletes recurrency in listing", %{conn: conn, recurrency: recurrency} do
      {:ok, index_live, _html} = live(conn, Routes.recurrency_index_path(conn, :index))

      assert index_live |> element("#recurrency-#{recurrency.id} a", "Delete") |> render_click()
      refute has_element?(index_live, "#recurrency-#{recurrency.id}")
    end
  end

  describe "Show" do
    setup [:create_recurrency]

    test "displays recurrency", %{conn: conn, recurrency: recurrency} do
      {:ok, _show_live, html} = live(conn, Routes.recurrency_show_path(conn, :show, recurrency))

      assert html =~ "Show Recurrency"
      assert html =~ recurrency.description
    end

    test "updates recurrency within modal", %{conn: conn, recurrency: recurrency} do
      {:ok, show_live, _html} = live(conn, Routes.recurrency_show_path(conn, :show, recurrency))

      assert show_live |> element("a", "Edit") |> render_click() =~
               "Edit Recurrency"

      assert_patch(show_live, Routes.recurrency_show_path(conn, :edit, recurrency))

      assert show_live
             |> form("#recurrency-form", recurrency: @invalid_attrs)
             |> render_change() =~ "is invalid"

      {:ok, _, html} =
        show_live
        |> form("#recurrency-form", recurrency: @update_attrs)
        |> render_submit()
        |> follow_redirect(conn, Routes.recurrency_show_path(conn, :show, recurrency))

      assert html =~ "Recurrency updated successfully"
      assert html =~ "some updated description"
    end
  end
end
