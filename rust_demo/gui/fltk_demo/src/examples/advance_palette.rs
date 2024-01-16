use fltk::app;
use fltk::app::{App, Scheme, Sender};
use fltk::enums::{Align, Color, Event, Font, FrameType};
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
const DETAIL_COLOR_CELL_SIZE: i32 = CELL_SIZE * 4;
/// 窗体尺寸
const WINDOW_SIZE: (i32, i32) = (
    CELL_SIZE * COUNT_MATRIX.0 + INDEX_COL_DIMENSION.0 + SPACE * 2 + DETAIL_COLOR_CELL_SIZE,
    CELL_SIZE * COUNT_MATRIX.1 + INDEX_ROW_DIMENSION.1,
);

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    // 主要颜色
    let main_color = Color::White;
    let mut window = Window::default()
        .with_label("Colors")
        .with_size(WINDOW_SIZE.0, WINDOW_SIZE.1)
        .center_screen();
    window.set_color(main_color);

    // 发送和接收颜色变更事件
    let (color_sender, color_receiver) = app::channel::<Color>();

    // 内容行
    let mut content_row = Flex::default_fill().row();
    content_row.set_pad(0);
    content_row.set_margin(0);

    // 纵向索引
    layout_index_col(main_color, &mut content_row);

    // 容纳纵向索引和色块
    let mut color_and_index_col = Flex::default().column();
    color_and_index_col.set_pad(0);
    color_and_index_col.set_margin(0);

    // 水平索引
    layout_index_row(main_color, &mut color_and_index_col);

    // 色块
    layout_color_cells(color_sender, &mut content_row);

    color_and_index_col.end();

    // 详情面板
    let mut color_handle_fn = layout_detail_panel(main_color, &mut content_row);

    content_row.end();

    window.end();
    window.show();

    while app.wait() {
        if let Some(color) = color_receiver.recv() {
            color_handle_fn(color);
        }
    }

    // app.run().expect("run app failed");
}

fn layout_index_col(main_color: Color, content_row: &mut Flex) {
    let mut index_col = Flex::default().column();
    index_col.set_pad(0);
    index_col.set_margin(0);

    let mut space = Frame::default();
    // space.set_frame(FrameType::BorderBox);
    space.set_color(main_color);
    for col_idx in 0..COUNT_MATRIX.1 {
        let mut label = Frame::default()
            .with_label((col_idx * COUNT_MATRIX.0).to_string().as_str())
            .with_align(Align::Inside | Align::Right);
        // label.set_frame(FrameType::BorderBox);
        label.set_label_font(Font::Courier);
        label.set_color(main_color);
        index_col.fixed(&label, CELL_SIZE);
    }

    index_col.end();
    content_row.fixed(&index_col, INDEX_COL_DIMENSION.0);
}

fn layout_index_row(main_color: Color, color_and_index_col: &mut Flex) {
    let mut index_row = Flex::default().row();
    index_row.set_pad(0);
    index_row.set_margin(0);

    for col_idx in 0..COUNT_MATRIX.0 {
        let mut label = Frame::default()
            .with_label(col_idx.to_string().as_str())
            .with_align(Align::Inside | Align::Bottom);
        label.set_label_font(Font::Courier);
        // label.set_frame(FrameType::BorderBox);
        label.set_color(main_color);
        index_row.fixed(&label, CELL_SIZE);
    }

    index_row.end();
    color_and_index_col.fixed(&index_row, INDEX_ROW_DIMENSION.1);
}

fn layout_color_cells(color_sender: Sender<Color>, content_row: &mut Flex) {
    let mut color_col = Flex::default().column();
    color_col.set_pad(0);
    color_col.set_margin(0);

    // 色块
    for row_idx in 0..COUNT_MATRIX.1 as u8 {
        let mut color_row = Flex::default().row();
        color_row.set_pad(0);
        for col_idx in 0..COUNT_MATRIX.0 as u8 {
            let mut cell = Frame::default().with_size(CELL_SIZE, CELL_SIZE);
            cell.set_frame(FrameType::BorderBox);
            let color = Color::by_index(row_idx * COUNT_MATRIX.0 as u8 + col_idx);
            cell.set_color(color);
            cell.handle({
                move |_cell, event| match event {
                    Event::Enter => {
                        color_sender.send(color);
                        true
                    }
                    _ => false,
                }
            });
            // cell.emit(color_sender, color);
        }
        color_row.end();
    }

    color_col.end();
    content_row.fixed(&color_col, CELL_SIZE * COUNT_MATRIX.0);
}

fn layout_detail_panel(main_color: Color, content_row: &mut Flex) -> impl FnMut(Color) {
    let mut detail_panel_row = Flex::default().row();
    detail_panel_row.set_pad(0);
    detail_panel_row.set_margin(0);

    let space = Frame::default();
    detail_panel_row.fixed(&space, SPACE);

    let mut detail_col = Flex::default().column();
    detail_col.set_pad(0);
    detail_col.set_margin(0);

    Frame::default();

    let mut detail_color_cell = Frame::default();
    detail_color_cell.set_frame(FrameType::BorderBox);
    detail_color_cell.set_color(main_color);
    detail_col.fixed(&detail_color_cell, DETAIL_COLOR_CELL_SIZE);

    Frame::default();

    detail_col.end();
    detail_panel_row.fixed(&detail_col, DETAIL_COLOR_CELL_SIZE);

    let space = Frame::default();
    detail_panel_row.fixed(&space, SPACE);

    detail_panel_row.end();
    content_row.fixed(&detail_panel_row, SPACE * 2 + DETAIL_COLOR_CELL_SIZE);
    move |color| {
        detail_color_cell.set_color(color);
        detail_color_cell.redraw();
    }
}
