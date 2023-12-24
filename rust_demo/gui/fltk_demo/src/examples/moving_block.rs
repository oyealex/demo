#![allow(dead_code)]

use std::cell::RefCell;
use std::rc::Rc;

use fltk::{app, draw};
use fltk::app::{App, Scheme};
use fltk::enums::{Color, Event};
use fltk::prelude::{GroupExt, WidgetBase, WidgetExt, WindowExt};
use fltk::window::Window;

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    let mut window = Window::default()
        .with_size(800, 600)
        .center_screen()
        .with_label("Move");
    window.end();
    window.show();

    let block_pos = Rc::from(RefCell::from((380, 280)));

    window.draw({
        let pos = block_pos.clone();
        move |_| {
            draw::set_draw_color(Color::Blue);
            draw::draw_rectf(pos.borrow().0, pos.borrow().1, 40, 40);
        }
    });

    window.handle({
        let pos = block_pos.clone();
        move |_, event| {
            match event {
                // Event::Focus => true,
                Event::Move => {
                    *pos.borrow_mut() = (app::event_coords().0 - 20, app::event_coords().1 - 20);
                    true
                }
                _ => false,
            }
        }
    });

    while app.wait() {
        window.redraw();
        app::sleep(0.00694); // 144FPS
    }
}
