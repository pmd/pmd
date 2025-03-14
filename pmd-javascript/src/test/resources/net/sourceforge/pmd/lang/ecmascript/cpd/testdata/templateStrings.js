export default class DrawLocation extends joint.shapes.basic.Generic {
    constructor(location: ILocation) {
        this.markup = `<g>
        <path class="location"/>
        <text x="0" y="0" text-anchor="middle" class="location-text"></text>

        <path class="location"/>
        <circle class="location-circle"/>
        ${drawIndicators.Check.markup}

      </g>`;
    }

}