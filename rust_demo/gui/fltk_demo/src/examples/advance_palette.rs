use fltk::app::{App, Scheme};
use fltk::enums::{Align, Color, Font, FrameType};
use fltk::frame::Frame;
use fltk::group::Flex;
use fltk::prelude::{GroupExt, WidgetBase, WidgetExt, WindowExt};
use fltk::window::Window;

/// 每个色块尺寸
const CELL_SIZE: i32 = 20;
/// 色块数量
const COUNT_MATRIX: (i32, i32) = (8, 32);
/// 纵向索引尺寸
const INDEX_COL_DIMENSION: (i32, i32) = (40, CELL_SIZE * COUNT_MATRIX.1);
/// 横向索引尺寸
const INDEX_ROW_DIMENSION: (i32, i32) = (CELL_SIZE * COUNT_MATRIX.0, 40);
/// 内容到窗口边界的距离
const SPACE: i32 = 20;
/// 详情色块尺寸
const DETAIL_COLOR_CELL_SIZE: i32 = CELL_SIZE * 3;
/// 窗体尺寸
const WINDOW_SIZE: (i32, i32) = (
    CELL_SIZE * COUNT_MATRIX.0 + INDEX_COL_DIMENSION.0 + SPACE * 2 + DETAIL_COLOR_CELL_SIZE,
    CELL_SIZE * COUNT_MATRIX.1 + INDEX_ROW_DIMENSION.1,
);

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    let main_color = Color::White;
    let mut window = Window::default()
        .with_label("Colors")
        .with_size(WINDOW_SIZE.0, WINDOW_SIZE.1)
        .center_screen();
    window.set_color(main_color);

    // Flex::debug(true);
    // 主要内容
    let mut content_row = Flex::default_fill().row();
    content_row.set_pad(0);
    content_row.set_margin(0);
    {
        // 纵向索引
        let mut index_col = Flex::default().column();
        index_col.set_pad(0);
        index_col.set_margin(0);
        {
            let mut space = Frame::default();
            space.set_frame(FrameType::BorderBox);
            space.set_color(main_color);
            for col_idx in 0..COUNT_MATRIX.1 {
                let mut label = Frame::default()
                    .with_label((col_idx * COUNT_MATRIX.0).to_string().as_str())
                    .with_align(Align::Inside | Align::Right);
                label.set_frame(FrameType::BorderBox);
                label.set_label_font(Font::Courier);
                label.set_color(main_color);
                index_col.fixed(&label, CELL_SIZE);
            }
        }
        index_col.end();
        content_row.fixed(&index_col, INDEX_COL_DIMENSION.0);

        let mut color_and_index_col = Flex::default().column();
        color_and_index_col.set_pad(0);
        color_and_index_col.set_margin(0);
        {
            // 水平索引
            let mut index_row = Flex::default().row();
            index_row.set_pad(0);
            index_row.set_margin(0);
            {
                for col_idx in 0..COUNT_MATRIX.0 {
                    let mut label = Frame::default()
                        .with_label(col_idx.to_string().as_str())
                        .with_align(Align::Inside | Align::Bottom);
                    label.set_label_font(Font::Courier);
                    label.set_frame(FrameType::BorderBox);
                    label.set_color(main_color);
                    index_row.fixed(&label, CELL_SIZE);
                }
            }
            index_row.end();
            color_and_index_col.fixed(&index_row, INDEX_ROW_DIMENSION.1);

            // 色块
            let mut color_col = Flex::default().column();
            color_col.set_pad(0);
            color_col.set_margin(0);
            {
                // 色块
                for row_idx in 0..COUNT_MATRIX.1 as u8 {
                    let mut color_row = Flex::default().row();
                    color_row.set_pad(0);
                    for col_idx in 0..COUNT_MATRIX.0 as u8 {
                        let mut cell = Frame::default().with_size(CELL_SIZE, CELL_SIZE);
                        cell.set_frame(FrameType::BorderBox);
                        cell.set_color(Color::by_index(row_idx * COUNT_MATRIX.0 as u8 + col_idx));
                    }
                    color_row.end();
                }
            }
            color_col.end();
            content_row.fixed(&color_col, CELL_SIZE * COUNT_MATRIX.0);
        }
        color_and_index_col.end();

        // 详情面板
        let mut detail_panel_row = Flex::default().row();
        detail_panel_row.set_pad(0);
        detail_panel_row.set_margin(0);
        {
            let space = Frame::default();
            detail_panel_row.fixed(&space, SPACE);

            let mut detail_col = Flex::default().column();
            detail_col.set_pad(0);
            detail_col.set_margin(0);
            {
                Frame::default();

                let mut detail_color_cell = Frame::default();
                detail_color_cell.set_frame(FrameType::BorderBox);
                detail_color_cell.set_color(Color::by_index(5));
                detail_col.fixed(&detail_color_cell, DETAIL_COLOR_CELL_SIZE);

                Frame::default();
            }
            detail_col.end();
            detail_panel_row.fixed(&detail_col, DETAIL_COLOR_CELL_SIZE);

            let space = Frame::default();
            detail_panel_row.fixed(&space, SPACE);
        }
        detail_panel_row.end();
    content_row.fixed(&detail_panel_row, SPACE *  2 + DETAIL_COLOR_CELL_SIZE);
    }
    content_row.end();

    window.end();
    window.show();

    app.run().expect("run app failed");
}