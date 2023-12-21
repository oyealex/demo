use iced::{Element, Error, Sandbox, Settings};
use iced::widget::text_input;

fn main() -> Result<(), Error> {
    Editor::run(Settings::default())
}

struct Editor {
    content: String,
}

#[derive(Debug, Clone)]
enum Message {
    Edit(String),
}

impl Sandbox for Editor {
    type Message = Message;

    fn new() -> Self {
        Self {
            content: "value".into(),
        }
    }

    fn title(&self) -> String {
        String::from("A Cool Editor")
    }

    fn update(&mut self, message: Message) {
        match message {
            Message::Edit(value) => self.content = value,
        }
    }

    fn view(&self) -> Element<'_, Message> {
        text_input("placeholder", &self.content)
            .on_input(Message::Edit)
            .into()
    }
}
