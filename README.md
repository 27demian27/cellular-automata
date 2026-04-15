# Cellular Automata Simulator
A simple application containing a 2D plane of cells.
These cells can be manipulated manually by drawing with the mouse. \
In addition, by pressing `spacebar` one can simulate the next generation of the 
plane. The default RuleSet is set to the Game of Life.

Controls:
- `left mousebutton` - Toggle cell state
- `left mousebutton (drag)` - Paint cells with selected brush
- `right mousebutton (drag)` - Panning around the grid
- `scroll wheel ` - Zooming in or out of the grid
- `spacebar` - Simulate next generation
- `control + Z` - Undo last paint

Features include: 
- RuleSet selection (pre-defined and custom rules)
- Alternating RuleSets (can make for some interesting patterns)
- Grid controls (randomizing, clearing, etc.)
- Saving and Loading of Grid state

### How to run
``mvn compile`` \
``mvn exec:java``