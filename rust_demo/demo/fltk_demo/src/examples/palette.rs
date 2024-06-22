#![allow(dead_code)]
use fltk::app::{App, Scheme};
use fltk::enums::{Color, FrameType};
use fltk::frame::Frame;
use fltk::group::Flex;
use fltk::prelude::{GroupExt, WidgetBase, WidgetExt, WindowExt};
use fltk::window::Window;

/// 每个色块尺寸
const CELL_SIZE: i32 = 20;
/// 色块数量
const COUNT_MATRIX: (i32, i32) = (8, 32);
/// 色块到背景的间距
const BG_MARGIN: i32 = 5;
/// 内容到窗口边界的距离
const SPACE: i32 = 50;
/// 窗体尺寸
const WINDOW_SIZE: (i32, i32) = (
    CELL_SIZE * COUNT_MATRIX.0 + (BG_MARGIN + SPACE) * 2,
    CELL_SIZE * COUNT_MATRIX.1 + (BG_MARGIN + SPACE) * 2,
);

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    let mut window = Window::default()
        .with_label("Colors")
        .with_size(WINDOW_SIZE.0, WINDOW_SIZE.1)
        .center_screen();
    window.set_color(Color::White);

    // 背景
    let mut bg_col = Flex::default_fill()
        .row()
        .with_size(
            CELL_SIZE * COUNT_MATRIX.0 + BG_MARGIN * 2,
            CELL_SIZE * COUNT_MATRIX.1 + BG_MARGIN * 2,
        )
        .center_of_parent();
    bg_col.set_color(Color::by_index(49));
    bg_col.set_frame(FrameType::FlatBox);
    bg_col.set_margin(BG_MARGIN - 1);
    bg_col.set_pad(0);
    {
        let mut color_col = Flex::default().column();
        color_col.set_frame(FrameType::BorderBox);
        color_col.set_pad(0);
        {
            for row_idx in 0..COUNT_MATRIX.1 as u8 {
                let mut color_row = Flex::default().row();
                color_row.set_pad(0);
                for col_idx in 0..COUNT_MATRIX.0 as u8 {
                    let mut cell = Frame::default().with_size(CELL_SIZE, CELL_SIZE);
                    cell.set_color(Color::by_index(row_idx * COUNT_MATRIX.0 as u8 + col_idx));
                    cell.set_frame(FrameType::BorderBox);
                }
                color_row.end();
            }
        }
        color_col.end();
    }
    bg_col.end();

    window.end();
    window.show();

    app.run().expect("run app failed");
}
