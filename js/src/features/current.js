import { directive } from '../directives.js'

function resolveHref(el, dir) {
    let href = el.getAttribute('href')
    if (href) return href
    if (dir.expression) return dir.expression
    return ''
}

/** `wire:current` — adds `data-current` when the link matches the location. */
export function registerCurrentDirective() {
    directive('current', ({ el, directive: dir }) => {
        let href = resolveHref(el, dir)
        let path = location.pathname
        let target = href.startsWith('http') ? new URL(href).pathname : href
        let exact = dir.modifiers.includes('exact') || dir.modifiers.includes('strict')
        let matches = path === target
        if (! exact && ! matches && target.length > 1) matches = path.startsWith(target)
        el.toggleAttribute('data-current', matches)
    })
}

export { resolveHref }
