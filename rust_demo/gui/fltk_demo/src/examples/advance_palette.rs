use fltk::app::{App, Scheme};
use fltk::enums::{Align, Color, FrameType};
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
const INDEX_ROW_DIMENSION: (i32, i32) = (CELL_SIZE * COUNT_MATRIX.0, 30);
/// 内容到窗口边界的距离
const SPACE: i32 = 20;
/// 窗体尺寸
const WINDOW_SIZE: (i32, i32) = (
    CELL_SIZE * COUNT_MATRIX.0 + INDEX_COL_DIMENSION.0,
    CELL_SIZE * COUNT_MATRIX.1 + INDEX_ROW_DIMENSION.1,
);

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    let mut window = Window::default()
        .with_label("Colors")
        .with_size(WINDOW_SIZE.0, WINDOW_SIZE.1)
        .center_screen();
    window.set_color(Color::White);

    // 主要内容
    let mut content_row = Flex::default_fill().row();
    {
        // 纵向索引
        let mut index_col = Flex::default().column();
        index_col.set_pad(0);
        index_col.set_margin(0);
        {
            Frame::default();
            for col_idx in 0..COUNT_MATRIX.1 {
                let label = Frame::default().with_label((col_idx * COUNT_MATRIX.0).to_string().as_str())
                    .with_align(Align::Inside | Align::Right);
                index_col.fixed(&label, CELL_SIZE);
            }
        }
        index_col.end();
        content_row.fixed(&index_col, INDEX_COL_DIMENSION.0);

        let mut color_and_index_col = Flex::default().column();
        {
            // 水平索引
            let mut index_row = Flex::default().row();
            index_row.set_pad(0);
            index_row.set_margin(0);
            {
                for col_idx in 0..COUNT_MATRIX.0 {
                    let label = Frame::default().with_label(col_idx.to_string().as_str())
                        .with_align(Align::Inside | Align::Bottom);
                    index_row.fixed(&label, CELL_SIZE);
                }
            }
            index_row.end();
            color_and_index_col.fixed(&index_row, INDEX_ROW_DIMENSION.1);

            // 色块
            let mut color_col = Flex::default().column();
            color_col.set_frame(FrameType::BorderBox);
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
    }
    content_row.end();

    window.end();
    window.show();

    app.run().expect("run app failed");
}
