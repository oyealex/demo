#![allow(dead_code)]

use std::cell::RefCell;
use std::rc::Rc;

use fltk::{
    app,
    draw::{
        draw_line, draw_point, draw_rect_fill, LineStyle, Offscreen, set_draw_color, set_line_style,
    },
    enums::{Color, Event, FrameType},
    frame::Frame,
    prelude::*,
    window::Window,
};

const WIDTH: i32 = 800;
const HEIGHT: i32 = 600;

enum Tool {
    /// 线条
    Line,
    /// 矩形
    RectangleFrame,
}

pub fn run() {
    let app = app::App::default().with_scheme(app::Scheme::Gtk);

    let mut wind = Window::default()
        .with_size(WIDTH, HEIGHT)
        .with_label("RustyPainter");
    let mut frame = Frame::default()
        .with_size(WIDTH - 10, HEIGHT - 10)
        .center_of(&wind);
    frame.set_color(Color::White);
    frame.set_frame(FrameType::DownBox);

    wind.end();
    wind.show();

    // We fill our offscreen with white
    let offs = Offscreen::new(frame.width(), frame.height()).unwrap();
    #[cfg(not(target_os = "macos"))]
    {
        offs.begin();
        draw_rect_fill(0, 0, WIDTH - 10, HEIGHT - 10, Color::White);
        offs.end();
    }

    let offs = Rc::from(RefCell::from(offs));

    frame.draw({
        let offs = offs.clone();
        move |_| {
            let mut offs = offs.borrow_mut();
            if offs.is_valid() {
                offs.rescale();
                offs.copy(5, 5, WIDTH - 10, HEIGHT - 10, 0, 0);
            } else {
                offs.begin();
                draw_rect_fill(0, 0, WIDTH - 10, HEIGHT - 10, Color::White);
                offs.copy(5, 5, WIDTH - 10, HEIGHT - 10, 0, 0);
                offs.end();
            }
        }
    });

    frame.handle({
        let mut x = 0;
        let mut y = 0;
        move |f, ev| {
            // println!("{ev}, coords {:?}, mouse {:?}", app::event_coords(), app::get_mouse());
            let offs = offs.borrow_mut();
            match ev {
                Event::Push => {
                    {
                        offs.begin();
                        set_draw_color(Color::Red);
                        set_line_style(LineStyle::CapRound, 3);
                        let coords = app::event_coords();
                        x = coords.0;
                        y = coords.1;
                        draw_point(x, y);
                        offs.end();
                    }
                    f.redraw();
                    set_line_style(LineStyle::Solid, 0);
                    true
                }
                Event::Drag => {
                    {
                        offs.begin();
                        set_draw_color(Color::Red);
                        set_line_style(LineStyle::CapRound, 3);
                        let coords = app::event_coords();
                        draw_line(x, y, coords.0, coords.1);
                        // draw_point(x, y);
                        x = coords.0;
                        y = coords.1;
                        offs.end();
                    }
                    f.redraw();
                    set_line_style(LineStyle::Solid, 0);
                    true
                }
                _ => false,
            }
        }
    });

    app.run().unwrap();
}
